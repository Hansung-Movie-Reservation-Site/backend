package com.springstudy.backend.API.Auth.Service;

import com.springstudy.backend.API.Auth.Model.Request.ChangeDetail.AddMyTheatherRequest;
import com.springstudy.backend.API.Auth.Model.Request.ChangeDetail.ChangeDetailRequest;
import com.springstudy.backend.API.Auth.Model.Request.ChangeDetail.ChangeEmailRequest;
import com.springstudy.backend.API.Auth.Model.Request.RetrieveRequest;
import com.springstudy.backend.API.Auth.Model.Response.ChangeResponse.AddTheatherResponse;
import com.springstudy.backend.API.Auth.Model.Response.ChangeResponse.ChangeDetailResponse;
import com.springstudy.backend.API.Auth.Model.Response.RetrieveResponse.RetrieveAIResponse;
import com.springstudy.backend.API.Auth.Model.Response.RetrieveResponse.RetrieveMyTheatherResponse;
import com.springstudy.backend.API.Auth.Model.Response.RetrieveResponse.RetrieveTicketResponse;
import com.springstudy.backend.API.Auth.Model.RetrieveResponse;
import com.springstudy.backend.API.Auth.Model.RetrieveType;
import com.springstudy.backend.API.Repository.Entity.*;
import com.springstudy.backend.API.Repository.MyTheatherRepository;
import com.springstudy.backend.API.Repository.SpotRepository;
import com.springstudy.backend.API.Repository.UserRepository;
import com.springstudy.backend.Common.CheckPasswordService;
import com.springstudy.backend.Common.ErrorCode.CustomException;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DetailService {

    private final UserRepository userRepository;
    private final CheckPasswordService checkPasswordService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ChangeDetailResponse changeDetail(ChangeDetailRequest changeDetailRequest, DetailType detailType) {
        // 사용자 정보 변경.
        // 1. 사용자 유무 확인.
        // 2. 비밀번호 확인.
        // 3. change 함수로 변경.

        Long user_id = changeDetailRequest.user_id();

        Optional<User> userOptional = userRepository.findById(user_id);
        if(userOptional.isEmpty()){
            //todo
            log.error("User not found");
            throw new CustomException(ErrorCode.NOT_EXIST_USER);
        }
        User user = userOptional.get();

        String before = changeDetailRequest.password();
        checkPassword(user, before, detailType);
        // 원래 정보가 맞는지 비밀번호로 확인한다.
        change(user, changeDetailRequest, detailType);
        return new ChangeDetailResponse(ErrorCode.SUCCESS);
    }
    private void checkPassword(User user, String before, DetailType detailType) {
        if(detailType == DetailType.EMAIL)return;
        checkPasswordService.checkPassword(user, before);
    }
    private void change(User user, ChangeDetailRequest changeDetailRequest, DetailType detailType) {
        String after = changeDetailRequest.after();

        switch (detailType) {
            case USERNAME: user.changeUsername(after);break;
            case PASSWORD: user.getUser_credentional().changePassword(passwordEncoder.encode(after));break;
            default: throw new CustomException(ErrorCode.ERROR_CHANGE_TYPE);
        }
    }

    @Transactional
    public ChangeDetailResponse changeEmail(ChangeEmailRequest changeEmailRequest) {
        Long user_id = changeEmailRequest.user_id();

        Optional<User> userOptional = userRepository.findById(user_id);
        if(userOptional.isEmpty()){
            //todo
            log.error("User not found");
            throw new CustomException(ErrorCode.NOT_EXIST_USER);
        }
        User user = userOptional.get();
        user.changeEmail(changeEmailRequest.after());
        return new ChangeDetailResponse(ErrorCode.SUCCESS);
    }

    public RetrieveResponse retrieve(RetrieveRequest retrieveRequest, RetrieveType retrieveType) {
        Long user_id = retrieveRequest.user_id();
        Optional<User> userOptional = userRepository.findById(user_id);
        if(userOptional.isEmpty()){
            log.error("User not found");
            throw new CustomException(ErrorCode.NOT_EXIST_USER);
        }
        User user = userOptional.get();
        RetrieveResponse response = makeResponse(user, retrieveType);

        return response;
    }
    public RetrieveResponse makeResponse(User user, RetrieveType retrieveType) {
        switch(retrieveType){
            case TICKET: List<Ticket> ticketList = user.getUserTickets();
                return new RetrieveTicketResponse(ErrorCode.SUCCESS, ticketList);
            case AI: List<AI> aiList = user.getAiList();
                return new RetrieveAIResponse(ErrorCode.SUCCESS, aiList);
            case MY_THEATHER: List<MyTheather> myTheather = user.getMyTheatherList();
                return new RetrieveMyTheatherResponse(ErrorCode.SUCCESS, myTheather);
            default: throw new CustomException(ErrorCode.ERROR_RETRIEVE_TYPE);
        }
    }


    @Transactional
    public AddTheatherResponse updateTheather(AddMyTheatherRequest addTheatherRequest) {
        Long user_id = addTheatherRequest.user_id();
        List<Long> user_SpotList = addTheatherRequest.mySpotList();

        Optional<User> userOptional = userRepository.findById(user_id);
        if(userOptional.isEmpty()){throw new CustomException(ErrorCode.NOT_EXIST_USER);}
        User user = userOptional.get();

        List<MyTheather> myTheatherList = new ArrayList<>();
        for(Long id: user_SpotList){
            MyTheather myTheather = MyTheather.builder()
                    .spot_id(id)
                    .user(user)
                    .build();
            myTheatherList.add(myTheather);
            System.out.println(myTheather.getId());
        }

        user.setMyTheatherList(myTheatherList);

        //if(result == null){throw new CustomException(ErrorCode.TRANSACTION_ERROR);}

        return new AddTheatherResponse(ErrorCode.SUCCESS, myTheatherList);
        }
}