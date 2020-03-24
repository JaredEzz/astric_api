package astric.server.dao;

import astric.model.dao.PostDAO;
import astric.model.domain.Post;
import astric.model.service.post.MakePostRequest;
import astric.model.service.response.post.MakePostResponse;

import java.util.ArrayList;
import java.util.List;

public class PostDAOImpl implements PostDAO {
    private List<Post> postList = new ArrayList<>();

    @Override
    public MakePostResponse makePost(MakePostRequest request) {
        Post post = request.getPost();
        postList.add(post);

        return new MakePostResponse(true);
    }
}
