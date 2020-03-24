package astric.model.service;

import astric.model.service.post.MakePostRequest;
import astric.model.service.request.account.LoginRequest;
import astric.model.service.request.account.LogoutRequest;
import astric.model.service.request.account.SignUpRequest;
import astric.model.service.response.account.LoginResponse;
import astric.model.service.response.account.LogoutResponse;
import astric.model.service.response.account.SignUpResponse;
import astric.model.service.response.post.MakePostResponse;

/**
 * Defines the interface for the 'account' service.
 */
public interface PostService {
    /**
     * Returns success if the post was made.
     *
     * @param request contains the data required to fulfill the request.
     * @return on success, true, else error.
     */
    MakePostResponse makePost(MakePostRequest request);
}
