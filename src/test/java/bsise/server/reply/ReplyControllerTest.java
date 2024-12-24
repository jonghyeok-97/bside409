package bsise.server.reply;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bsise.server.letter.Letter;
import bsise.server.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ReplyControllerTest {

    @InjectMocks
    private ReplyController replyController;

    @Mock
    private ReplyService replyService;

    private MockMvc mockMvc;
    private User mockUser;
    private Letter mockLetter;
    private Reply mockReply;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(replyController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        /* Mocking User 객체 */
        mockUser = mock(User.class);
        UUID userId = UUID.randomUUID();
        given(mockUser.getId()).willReturn(userId);

        /* Mocking Letter 객체 */
        mockLetter = mock(Letter.class);
        given(mockLetter.getUser()).willReturn(mockUser);

        /* Mocking Reply 객체 */
        mockReply = mock(Reply.class);
        UUID replyId = UUID.randomUUID();
        given(mockReply.getId()).willReturn(replyId);
        given(mockReply.getLetter()).willReturn(mockLetter);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void findMyLetters(boolean published) throws Exception {
        // given
        /* Arrange */
        int year = 2024;
        PageRequest pageable = PageRequest.of(0, 15);
        given(mockLetter.isPublished()).willReturn(published); // published 조건에 따른 편지의 공개 여부 stub
        given(mockReply.getCreatedAt()).willReturn(LocalDateTime.of(year, 1, 1, 0, 0));

        PageImpl<ReplyResponseDto> replies = new PageImpl<>(List.of(ReplyResponseDto.of(mockReply)), pageable, 1);
        given(replyService.findMyLetterAndReply(mockUser.getId(), year, published, pageable)).willReturn(replies);

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/replies/users/{userId}", mockUser.getId())
                        .queryParam("year", String.valueOf(year))
                        .queryParam("published", String.valueOf(published))
                        .queryParam("page", String.valueOf(pageable.getPageNumber()))
                        .queryParam("size", String.valueOf(pageable.getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        ReplyResponseDto firstContent = replies.getContent().get(0);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$..[?(@.userId == '%s')]", firstContent.getUserId()).exists())
                .andExpect(jsonPath("$..[?(@.replyId == '%s')]", firstContent.getReplyId()).exists())
                .andExpect(jsonPath("$..[?(@.published == %s)]", firstContent.isPublished()).exists())
                .andExpect(jsonPath("$..[?(@.pageNumber == %d)]", replies.getNumber()).exists())
                .andExpect(jsonPath("$..[?(@.pageSize == %d)]", replies.getSize()).exists())
                .andDo(MockMvcResultHandlers.print());
    }
}