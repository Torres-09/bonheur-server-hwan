package com.bonheur.domain.board.controller;

import com.bonheur.config.interceptor.Auth;
import com.bonheur.config.resolver.MemberId;
import com.bonheur.config.swagger.dto.ApiDocumentResponse;
import com.bonheur.domain.board.model.dto.*;
import com.bonheur.domain.board.service.BoardService;
import com.bonheur.domain.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class BoardController {
    private final BoardService boardService;

    @ApiDocumentResponse
    @Operation(summary = "행복기록 전체 조회")
    @GetMapping("/api/boards")
    @Auth
    public ApiResponse<GetBoardsGroupsResponse> getAllBoards(@Valid @MemberId Long memberId,
                                                             @RequestParam(required = false) String orderType,
                                                             @RequestParam(required = false) Long lastBoardId,
                                                             @PageableDefault(size = 5) Pageable pageable) {
        GetBoardsRequest request = GetBoardsRequest.of(orderType, lastBoardId);
        Slice<GetBoardsResponse> getBoardsResponses = boardService.getAllBoards(memberId, request, pageable);
        GetBoardsGroupsResponse getBoardsGroupsResponse = boardService.getBoardsGroups(getBoardsResponses, request.getOrderType());

        return ApiResponse.success(getBoardsGroupsResponse);
    }

    // # 게시글 삭제
    @ApiDocumentResponse
    @Operation(summary = "행복기록 삭제")
    @DeleteMapping("/api/boards/{boardId}")
    @Auth
    public ApiResponse<DeleteBoardResponse> deleteBoard(@Valid @MemberId Long memberId, @PathVariable("boardId") Long boardId) {
        DeleteBoardResponse deleteBoardResponse = boardService.deleteBoard(memberId, boardId);
        return ApiResponse.success(deleteBoardResponse);
    }

    // # 게시글 조회 - 해시태그
    @ApiDocumentResponse
    @Operation(summary = "행복기록 조회 - 해시태그")
    @ResponseBody
    @PostMapping ("/api/boards/tag")
    @Auth
    public ApiResponse<GetBoardsGroupsResponse> getBoardsByTag(@Valid @MemberId Long memberId,
                                                               @RequestBody GetBoardByTagRequest tagRequest,
                                                               @PageableDefault(size = 5) Pageable pageable) {
        GetBoardsRequest getBoardsRequest = GetBoardsRequest.of(tagRequest.getOrderType(), tagRequest.getLastBoardId());
        Slice<GetBoardsResponse> getBoardsResponses =
                boardService.getBoardsByTag(memberId, getBoardsRequest, tagRequest, pageable);
        GetBoardsGroupsResponse getBoardsGroupsResponse = boardService.getBoardsGroups(getBoardsResponses, getBoardsRequest.getOrderType());

        return ApiResponse.success(getBoardsGroupsResponse);
    }

    // # 게시글 조회 - by 날짜
    @ApiDocumentResponse
    @Operation(summary = "행복기록 조회 - 날짜별")
    @GetMapping("/api/boards/date")
    @Auth
    public ApiResponse<GetBoardsByDateResponse> getBoardsByDate(@Valid @MemberId Long memberId,
                                                                @RequestParam(required = false) String orderType,
                                                                @RequestParam(required = false) Long lastBoardId,
                                                                @RequestParam String localDate,
                                                                @PageableDefault(size = 5) Pageable pageable) {
        GetBoardsRequest request = GetBoardsRequest.of(orderType, lastBoardId);
        Slice<GetBoardsResponse> getBoardsResponses = boardService.getBoardsByDate(memberId, request, localDate, pageable);
        Long numOfBoardsByDate = boardService.getNumOfBoardsByDate(memberId, localDate);

        GetBoardsByDateResponse getBoardsByDateResponse = GetBoardsByDateResponse.of(numOfBoardsByDate, getBoardsResponses);
        return ApiResponse.success(getBoardsByDateResponse);
    }

    // # 캘린더 화면
    @ApiDocumentResponse
    @Operation(summary = "행복기록 캘린더 - 작성여부")
    @GetMapping("/api/calendar")
    @Auth
    public ApiResponse<List<GetCalendarResponse>> getCalendar(@Valid @MemberId Long memberId, @RequestParam int year, @RequestParam int month) {
        List<GetCalendarResponse> getCalendarResponseList = boardService.getCalendar(memberId, year, month);
        return ApiResponse.success(getCalendarResponseList);
    }

    @ApiDocumentResponse
    @Operation(summary = "게시물 생성")
    @PostMapping("/api/boards")
    @Auth
    public ApiResponse<CreateBoardResponse> createBoard(
            @Valid @MemberId Long memberId,
            @RequestPart(required = false, value = "images") List<MultipartFile> images,
            @RequestPart @Valid CreateBoardRequest createBoardRequest) throws IOException {

        return ApiResponse.success(boardService.createBoard(memberId, createBoardRequest, images));
    }

    @ApiDocumentResponse
    @Operation(summary = "게시물 수정")
    @PatchMapping("/api/boards/{boardId}")
    @Auth
    public ApiResponse<UpdateBoardResponse> updateBoard(
            @Valid @MemberId Long memberId,
            @PathVariable("boardId") Long boardId,
            @RequestPart(required = false, value = "images") List<MultipartFile> images,
            @RequestPart @Valid UpdateBoardRequest updateBoardRequest) throws IOException {

        return ApiResponse.success(boardService.updateBoard(memberId, boardId, updateBoardRequest, images));
    }

    @ApiDocumentResponse
    @Operation(summary = "게시물 상세 조회")
    // 이상 Swagger 코드
    @GetMapping("/api/boards/{boardId}")
    @Auth
    public ApiResponse<GetBoardResponse> getBoard(
            @PathVariable Long boardId,
            @Valid @MemberId Long memberId
    ) {
        return ApiResponse.success(boardService.getBoard(memberId, boardId));
    }
}