package com.api.TaveShot.domain.newsletter.admin.service;

import com.api.TaveShot.domain.newsletter.admin.dto.*;
import com.api.TaveShot.domain.newsletter.admin.editor.EventEditor;
import com.api.TaveShot.domain.newsletter.client.domain.Subscription;
import com.api.TaveShot.domain.newsletter.client.service.EmailSenderService;
import com.api.TaveShot.domain.newsletter.client.repository.SubscriptionRepository;
import com.api.TaveShot.domain.newsletter.domain.Event;
import com.api.TaveShot.domain.newsletter.domain.LetterType;
import com.api.TaveShot.domain.newsletter.domain.Newsletter;
import com.api.TaveShot.domain.newsletter.admin.repository.EventRepository;
import com.api.TaveShot.domain.newsletter.letter.dto.NewsletterCreateRequest;
import com.api.TaveShot.domain.newsletter.letter.dto.NewsletterResponse;
import com.api.TaveShot.domain.newsletter.letter.repository.NewsletterRepository;
import com.api.TaveShot.domain.newsletter.letter.service.TemplateService;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminEventService {

    private final EventRepository eventRepository;
    private final NewsletterRepository newsletterRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final EmailSenderService emailSenderService;
    private final TemplateService templateService;

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
        return date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) - LocalDate.of(date.getYear(), date.getMonth(), 1).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) + 1;
    }

    private String getMonthInKorean(LocalDate date) {
        return date.getMonth().getDisplayName(TextStyle.FULL, Locale.KOREAN);
    }

    @Transactional
    public NewsletterResponse createWeeklyNewsletter(LocalDate endOfWeek) {
        LocalDate startOfWeek = endOfWeek.minusDays(7);

        List<Event> events = eventRepository.findEventsForNewsletter(endOfWeek, startOfWeek);

        Map<LetterType, List<Event>> letterTypeEventsMap = events.stream()
                .collect(Collectors.groupingBy(Event::getLetterType));

        Long devNewsletterId = createNewsletterForLetterType(letterTypeEventsMap, LetterType.DEV_LETTER, endOfWeek);
        Long employeeNewsletterId = createNewsletterForLetterType(letterTypeEventsMap, LetterType.EMPLOYEE_LETTER, endOfWeek);

        Map<LetterType, Long> newsletterIds = Map.of(
                LetterType.DEV_LETTER, devNewsletterId,
                LetterType.EMPLOYEE_LETTER, employeeNewsletterId
        );

        return new NewsletterResponse(newsletterIds);
    }

    private Long createNewsletterForLetterType(Map<LetterType, List<Event>> letterTypeEventsMap, LetterType letterType, LocalDate endOfWeek) {
        int weekOfMonth = getWeekOfMonth(endOfWeek);
        String month = getMonthInKorean(endOfWeek);

        String newsletterTitle = String.format("%s %d째주 %s", month, weekOfMonth, letterType.toString());
        List<Event> events = letterTypeEventsMap.getOrDefault(letterType, Collections.emptyList());

        List<EventSingleResponse> eventDtos = events.stream()
                .map(EventSingleResponse::from)
                .toList();

        String templateName = letterType == LetterType.DEV_LETTER ? "dev_newsletter" : "employee_newsletter";
        String content = templateService.renderHtmlContent(eventDtos, newsletterTitle, templateName);

        Newsletter newsletter = Newsletter.createNewsletter(
                new NewsletterCreateRequest(newsletterTitle, content, letterType.toString()),
                events
        );

        Newsletter savedNewsletter = newsletterRepository.save(newsletter);
        return savedNewsletter.getId();
    }

    @Transactional
    public void sendWeeklyNewsletter(LocalDate endOfWeek) throws MessagingException {
        NewsletterResponse newsletterResponse = createWeeklyNewsletter(endOfWeek);

        Newsletter devNewsletter = newsletterRepository.findById(newsletterResponse.newsletterIds().get(LetterType.DEV_LETTER))
                .orElseThrow(() -> new ApiException(ErrorType.NEWSLETTER_NOT_FOUND));
        Newsletter employeeNewsletter = newsletterRepository.findById(newsletterResponse.newsletterIds().get(LetterType.EMPLOYEE_LETTER))
                .orElseThrow(() -> new ApiException(ErrorType.NEWSLETTER_NOT_FOUND));

        // 정렬된 이벤트 리스트를 다시 렌더링
        List<EventSingleResponse> devEvents = new ArrayList<>(devNewsletter.getEvents().stream()
                .map(EventSingleResponse::from)
                .sorted(Comparator.comparing(EventSingleResponse::startDate).thenComparing(EventSingleResponse::endDate))
                .collect(Collectors.toList()));
        String devContent = templateService.renderHtmlContent(devEvents, devNewsletter.getTitle(), "dev_newsletter.html");

        List<EventSingleResponse> employeeEvents = new ArrayList<>(employeeNewsletter.getEvents().stream()
                .map(EventSingleResponse::from)
                .sorted(Comparator.comparing(EventSingleResponse::startDate).thenComparing(EventSingleResponse::endDate))
                .collect(Collectors.toList()));
        String employeeContent = templateService.renderHtmlContent(employeeEvents, employeeNewsletter.getTitle(), "employee_newsletter.html");

        sendEmailsToSubscribers(devNewsletter, devContent);
        sendEmailsToSubscribers(employeeNewsletter, employeeContent);

        // 뉴스레터 전송 후 상태 업데이트
        devNewsletter.letterSent();
        employeeNewsletter.letterSent();
    }

    private void sendEmailsToSubscribers(Newsletter newsletter, String content) throws MessagingException {
        List<Subscription> subscriptions = subscriptionRepository.findAllByLetterType(newsletter.getLetterType());
        for (Subscription subscription : subscriptions) {
            if (canSend(subscription.getLetterType(), newsletter.getLetterType())) {
                emailSenderService.sendEmail(subscription.getMember().getGitEmail(), newsletter.getTitle(), content);
            }
        }
    }

    private boolean canSend(LetterType subscribedType, LetterType newsletterType) {
        return subscribedType == LetterType.ALL || subscribedType == newsletterType;
    }

    @Scheduled(cron = "0 0/1 * * * ?") // 테스트용
    public void scheduledNewsletter() throws MessagingException {
        sendWeeklyNewsletter(LocalDate.now());
    }
}


/*@Scheduled(cron = "0 0 8 * * MON") // 매주 월요일 8시에 실행
    public void scheduledNewsletter() throws MessagingException {
        sendWeeklyNewsletter();
    }*/
