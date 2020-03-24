package astric.model.dao;

import astric.model.service.post.MakePostRequest;
import astric.model.service.request.account.LoginRequest;
import astric.model.service.request.account.LogoutRequest;
import astric.model.service.request.account.SignUpRequest;
import astric.model.service.response.account.LoginResponse;
import astric.model.service.response.account.LogoutResponse;
import astric.model.service.response.account.SignUpResponse;
import astric.model.service.response.post.MakePostResponse;

public interface PostDAO {
    MakePostResponse makePost(MakePostRequest request);
}
