package astric.server.service;

import astric.model.dao.PostDAO;
import astric.model.service.PostService;
import astric.model.service.post.MakePostRequest;
import astric.model.service.response.post.MakePostResponse;
import astric.server.dao.PostDAOImpl;

public class PostServiceImpl implements PostService {
    @Override
    public MakePostResponse makePost(MakePostRequest request) {
        PostDAO dao = new PostDAOImpl();
        return dao.makePost(request);
    }
}
