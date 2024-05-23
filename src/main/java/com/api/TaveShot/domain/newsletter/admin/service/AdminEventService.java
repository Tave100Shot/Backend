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
                .collect(Collectors.toList());
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
        EventEditor editor = editorBuilder.title(request.title())
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
    public Map<LetterType, Long> createWeeklyNewsletter() {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        Map<LetterType, List<Event>> letterTypeEventsMap = new HashMap<>();

        // 이벤트 레터 타입에 따라 맵에 그룹화
        for (Event event : eventRepository.findAll()) {
            if (!event.isDeleted() && event.getStartDate().isBefore(endOfWeek.plusDays(1)) && event.getEndDate().isAfter(startOfWeek.minusDays(1))) {
                LetterType letterType = event.getLetterType();
                if (!letterTypeEventsMap.containsKey(letterType)) {
                    letterTypeEventsMap.put(letterType, new ArrayList<>());
                }
                letterTypeEventsMap.get(letterType).add(event);
            }
        }

        // 행사 시작 날짜 기준으로 정렬, 시작 날짜가 같으면 종료 날짜 기준으로 정렬
        for (List<Event> events : letterTypeEventsMap.values()) {
            events.sort(Comparator.comparing(Event::getStartDate).thenComparing(Event::getEndDate));
        }

        // 뉴스레터 생성
        Long devNewsletterId = createNewsletterForLetterType(letterTypeEventsMap, LetterType.DEV_LETTER, startOfWeek);
        Long employeeNewsletterId = createNewsletterForLetterType(letterTypeEventsMap, LetterType.EMPLOYEE_LETTER, startOfWeek);

        Map<LetterType, Long> newsletterIds = new HashMap<>();
        newsletterIds.put(LetterType.DEV_LETTER, devNewsletterId);
        newsletterIds.put(LetterType.EMPLOYEE_LETTER, employeeNewsletterId);

        return newsletterIds;
    }

    private Long createNewsletterForLetterType(Map<LetterType, List<Event>> letterTypeEventsMap, LetterType letterType, LocalDate startOfWeek) {
        int weekOfMonth = getWeekOfMonth(startOfWeek);
        String month = getMonthInKorean(startOfWeek);

        String newsletterTitle = String.format("%s %d째주 %s Newsletter", month, weekOfMonth, letterType.toString());
        List<Event> events = letterTypeEventsMap.getOrDefault(letterType, Collections.emptyList());

        events.sort(Comparator.comparing(Event::getStartDate).thenComparing(Event::getEndDate));

        List<EventSingleResponse> eventDtos = events.stream()
                .map(EventSingleResponse::from)
                .collect(Collectors.toList());

        String templateName = letterType == LetterType.DEV_LETTER ? "dev_newsletter" : "employee_newsletter";
        String content = templateService.renderHtmlContent(eventDtos, newsletterTitle, templateName);

        Newsletter newsletter = Newsletter.createNewsletter(
                new NewsletterCreateRequest(newsletterTitle, content, letterType.toString()),
                events
        );

        newsletterRepository.save(newsletter);

        return newsletter.getId();
    }

    @Transactional
    public void sendWeeklyNewsletter(LocalDate now) throws MessagingException {
        Map<LetterType, Long> newsletterIds = createWeeklyNewsletter();

        Newsletter devNewsletter = newsletterRepository.findById(newsletterIds.get(LetterType.DEV_LETTER))
                .orElseThrow(() -> new ApiException(ErrorType.NEWSLETTER_NOT_FOUND));
        Newsletter employeeNewsletter = newsletterRepository.findById(newsletterIds.get(LetterType.EMPLOYEE_LETTER))
                .orElseThrow(() -> new ApiException(ErrorType.NEWSLETTER_NOT_FOUND));

        // 정렬된 이벤트 리스트를 다시 렌더링
        List<EventSingleResponse> devEvents = devNewsletter.getEvents().stream().map(EventSingleResponse::from).collect(Collectors.toList());
        devEvents.sort(Comparator.comparing(EventSingleResponse::startDate).thenComparing(EventSingleResponse::endDate));
        String devContent = templateService.renderHtmlContent(devEvents, devNewsletter.getTitle(), "dev_newsletter.html");

        List<EventSingleResponse> employeeEvents = employeeNewsletter.getEvents().stream().map(EventSingleResponse::from).collect(Collectors.toList());
        employeeEvents.sort(Comparator.comparing(EventSingleResponse::startDate).thenComparing(EventSingleResponse::endDate));
        String employeeContent = templateService.renderHtmlContent(employeeEvents, employeeNewsletter.getTitle(), "employee_newsletter.html");

        sendEmailsToSubscribers(devNewsletter, devContent);
        sendEmailsToSubscribers(employeeNewsletter, employeeContent);

        // 뉴스레터 전송 후 상태 업데이트
        devNewsletter.letterSent();
        employeeNewsletter.letterSent();
    }

    private void sendEmailsToSubscribers(Newsletter newsletter, String content) throws MessagingException {
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        for (Subscription subscription : subscriptions) {
            if (canSend(subscription.getLetterType(), newsletter.getLetterType())) {
                emailSenderService.sendEmail(subscription.getMember().getGitEmail(), newsletter.getTitle(), content);
            }
        }
    }

    private boolean canSend(LetterType subscribedType, LetterType newsletterType) {
        if (subscribedType == LetterType.ALL) {
            return newsletterType == LetterType.DEV_LETTER || newsletterType == LetterType.EMPLOYEE_LETTER;
        }
        return subscribedType == newsletterType;
    }

    /*@Scheduled(cron = "0 0 8 * * MON") // 매주 월요일 8시에 실행
    public void scheduledNewsletter() throws MessagingException {
        sendWeeklyNewsletter();
    }*/

    @Scheduled(cron = "0 0/1 * * * ?") // 테스트용
    public void scheduledNewsletter() throws MessagingException {
        sendWeeklyNewsletter(LocalDate.now());
    }
}

