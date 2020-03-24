package astric.server.dao;

import astric.model.dao.PostDAO;
import astric.model.domain.Post;
import astric.model.domain.User;
import astric.model.service.request.post.FeedRequest;
import astric.model.service.request.post.MakePostRequest;
import astric.model.service.response.post.FeedResponse;
import astric.model.service.response.post.MakePostResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostDAOImpl implements PostDAO {
    private List<Post> postList = new ArrayList<>();

    private List<User> hardCodedUsers = Arrays.asList(
            new User("Jared", "Hasson", "@jaredezz", "assets/images/astric.png", "jaredhasson"),
            new User("Thomas", "Banks", "@tb", "assets/images/man_profile.png", "tbanks"),
            new User("Wendy", "Watts", "@wwatts", "assets/images/woman_profile.png", "wendyw"),
            new User("Orville", "Klaus", "@santa", "assets/images/santa.png", "ovk"),
            new User("Manny", "Woodpecker", "@birdlover", "assets/images/woodpecker.jpg", "mannywp"),
            new User("Fanny", "Follower", "@ff", "assets/images/fanny_pack.jpeg", "follo"));

    private List<Post> hardCodedPosts = Arrays.asList(
            new Post(hardCodedUsers.get(0), "20121002", "This is a good website https://google.com @tb @birdlover"),
            new Post(hardCodedUsers.get(0), "20121002", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate"),
            new Post(hardCodedUsers.get(1), "20121002", "Tweet 3"),
            new Post(hardCodedUsers.get(2), "20121002", "Tweet 4"),
            new Post(hardCodedUsers.get(3), "20121002", "Tweet 5"),
            new Post(hardCodedUsers.get(4), "20121002", "Tweet 7"),
            new Post(hardCodedUsers.get(1), "20121002", "Tweet 6"),
            new Post(hardCodedUsers.get(5), "20121002", "I love to go backpacking")
    );

    @Override
    public MakePostResponse makePost(MakePostRequest request) {
        Post post = request.getPost();
        postList.add(post);

        return new MakePostResponse(true);
    }

    @Override
    public FeedResponse getFeed(FeedRequest request) {
        assert request.getLimit() > 0;
        assert request.getUsername() != null;

        //make a list of the posts to return based on if the user is
        //following the originating user or is the originating user

        List<Post> allPosts = hardCodedPosts; //TODO filter hardcoded posts by user/follower
        List<Post> responsePosts = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if (request.getLimit() > 0) {
            int feedIndex = getFeedStartingIndex(request.getLastPost(), allPosts);

            for (int limitCounter = 0; feedIndex < allPosts.size() && limitCounter < request.getLimit(); feedIndex++, limitCounter++) {
                responsePosts.add(allPosts.get(feedIndex));
            }

            hasMorePages = feedIndex < allPosts.size();
        }

        return new FeedResponse(responsePosts, hasMorePages);
    }

    private int getFeedStartingIndex(Post lastPost, List<Post> allPosts){
        int feedIndex = 0;
        if (lastPost != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allPosts.size(); i++) {
                if(lastPost.equals(allPosts.get(i))) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    feedIndex = i + 1;
                }
            }
        }
        return feedIndex;
    }
}
