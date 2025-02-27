package com.api.TaveShot.domain.newsletter.admin.service;

import com.api.TaveShot.domain.newsletter.admin.dto.EventCreateRequest;
import com.api.TaveShot.domain.newsletter.admin.dto.EventSingleResponse;
import com.api.TaveShot.domain.newsletter.admin.dto.EventUpdateRequest;
import com.api.TaveShot.domain.newsletter.admin.editor.EventEditor;
import com.api.TaveShot.domain.newsletter.admin.repository.EventRepository;
import com.api.TaveShot.domain.newsletter.admin.repository.NewsletterEventRepository;
import com.api.TaveShot.domain.newsletter.client.repository.SubscriptionRepository;
import com.api.TaveShot.domain.newsletter.domain.Event;
import com.api.TaveShot.domain.newsletter.domain.LetterType;
import com.api.TaveShot.domain.newsletter.domain.Newsletter;
import com.api.TaveShot.domain.newsletter.domain.NewsletterEvent;
import com.api.TaveShot.domain.newsletter.event.NewsletterCreatedEvent;
import com.api.TaveShot.domain.newsletter.letter.dto.NewsletterCreateRequest;
import com.api.TaveShot.domain.newsletter.letter.dto.NewsletterResponse;
import com.api.TaveShot.domain.newsletter.letter.repository.NewsletterRepository;
import com.api.TaveShot.domain.newsletter.letter.service.TemplateService;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import jakarta.mail.MessagingException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminEventService {

    private final EventRepository eventRepository;
    private final NewsletterRepository newsletterRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final TemplateService templateService;
    private final ApplicationEventPublisher eventPublisher;
    private final NewsletterEventRepository newsletterEventRepository;

    @Transactional
    public Long register(final EventCreateRequest request) {
        Event createEvent = Event.from(request);
        Event event = eventRepository.save(createEvent);
        return event.getId();
    }

    public EventSingleResponse findById(final Long eventId) {
        Event findEvent = eventRepository.findByIdActivated(eventId);
        return EventSingleResponse.from(findEvent);
    }

    public List<EventSingleResponse> findEventsByDate(LocalDate date) {
        List<Event> events = eventRepository.findEventsByDate(date);
        if (events.isEmpty()) {
            throw new ApiException(ErrorType._EVENT_NOT_FOUND);
        }
        return events.stream()
                .map(EventSingleResponse::from)
                .toList();
    }

    @Transactional
    public Long delete(final Long eventId) {
        Event event = eventRepository.findByIdActivated(eventId);
        event.deactivate();
        return event.getId();
    }

    @Transactional
    public Long edit(final EventUpdateRequest request) {
        Long eventId = request.eventId();
        Event findEvent = eventRepository.findByIdActivated(eventId);

        EventEditor.EventEditorBuilder editorBuilder = findEvent.initEditor();
        EventEditor editor = editorBuilder
                .title(request.title())
                .content(request.content())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .build();

        findEvent.edit(editor);
        return eventId;
    }

    private int getWeekOfMonth(LocalDate date) {
        return date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) - LocalDate.of(date.getYear(), date.getMonth(), 1)
                .get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) + 1;
    }

    private String getMonthInKorean(LocalDate date) {
        return date.getMonth().getDisplayName(TextStyle.FULL, Locale.KOREAN);
    }

    @Transactional
    public NewsletterResponse createWeeklyNewsletter(LocalDate endOfWeek) {
        LocalDate startOfWeek = endOfWeek.minusDays(6); // 주간의 시작 날짜 계산

        // 주간의 시작 날짜와 끝나는 날짜를 기준으로 이벤트 취합
        List<Event> events = eventRepository.findEventsForNewsletter(startOfWeek, endOfWeek);

        List<Event> devEvents = new ArrayList<>();
        List<Event> employeeEvents = new ArrayList<>();

        for (Event event : events) {
            if (event.getLetterType() == LetterType.DEV_LETTER) {
                devEvents.add(event);
            } else if (event.getLetterType() == LetterType.EMPLOYEE_LETTER) {
                employeeEvents.add(event);
            }
        }

        Newsletter devNewsletter = createNewsletterForLetterType(devEvents, LetterType.DEV_LETTER, endOfWeek);
        Newsletter employeeNewsletter = createNewsletterForLetterType(employeeEvents, LetterType.EMPLOYEE_LETTER, endOfWeek);

        return new NewsletterResponse(devNewsletter, employeeNewsletter);
    }

    private Newsletter createNewsletterForLetterType(List<Event> events, LetterType letterType, LocalDate endOfWeek) {
        int weekOfMonth = getWeekOfMonth(endOfWeek);
        String month = getMonthInKorean(endOfWeek);

        String newsletterTitle = String.format("%s %d째주 %s", month, weekOfMonth, letterType.toString());

        List<EventSingleResponse> eventDtos = events.stream()
                .map(EventSingleResponse::from)
                .toList();

        String templateName = letterType == LetterType.DEV_LETTER ? "dev_newsletter" : "employee_newsletter";
        String content = templateService.renderHtmlContent(eventDtos, newsletterTitle, templateName);

        Newsletter newsletter = Newsletter.createNewsletter(
                new NewsletterCreateRequest(newsletterTitle, content, letterType.toString())
        );

        newsletterRepository.save(newsletter);

        List<NewsletterEvent> newsletterEvents = events.stream()
                .map(event -> new NewsletterEvent(newsletter, event))
                .toList();

        newsletterEventRepository.saveAll(newsletterEvents);
        return newsletter;
    }

    @Transactional
    public void sendWeeklyNewsletter(LocalDate endOfWeek) throws MessagingException {
        NewsletterResponse newsletterResponse = createWeeklyNewsletter(endOfWeek);

        Newsletter devNewsletter = newsletterResponse.devNewsletter();
        Newsletter employeeNewsletter = newsletterResponse.employeeNewsletter();

        // 정렬된 이벤트 리스트 가져오기
        List<EventSingleResponse> devEvents = getSortedEventSingleResponses(devNewsletter);
        String devContent = templateService.renderHtmlContent(devEvents, devNewsletter.getTitle(),
                "dev_newsletter.html");

        List<EventSingleResponse> employeeEvents = getSortedEventSingleResponses(employeeNewsletter);
        String employeeContent = templateService.renderHtmlContent(employeeEvents, employeeNewsletter.getTitle(),
                "employee_newsletter.html");

        // 이메일 수신자 목록을 미리 로드
        List<String> devRecipientEmails = subscriptionRepository.findAllByLetterType(devNewsletter.getLetterType())
                .stream()
                .map(subscription -> subscription.getMember().getGitEmail())
                .toList();

        List<String> employeeRecipientEmails = subscriptionRepository.findAllByLetterType(
                        employeeNewsletter.getLetterType()).stream()
                .map(subscription -> subscription.getMember().getGitEmail())
                .toList();

        // 이벤트 발행
        eventPublisher.publishEvent(new NewsletterCreatedEvent(devNewsletter, devContent, devRecipientEmails));
        eventPublisher.publishEvent(
                new NewsletterCreatedEvent(employeeNewsletter, employeeContent, employeeRecipientEmails));
    }

    private List<EventSingleResponse> getSortedEventSingleResponses(final Newsletter newsletter) {
        return eventRepository.getSortedEventSingleResponses(newsletter);
    }

    /*@Scheduled(cron = "0 0/1 * * * ?") // 테스트용
    public void scheduledNewsletter() throws MessagingException {
        sendWeeklyNewsletter(LocalDate.now());
    }*/

    @Scheduled(cron = "0 0 8 * * MON") // 매주 월요일 8시에 실행
    public void scheduledNewsletter() throws MessagingException {
        sendWeeklyNewsletter(LocalDate.now());
    }
}
