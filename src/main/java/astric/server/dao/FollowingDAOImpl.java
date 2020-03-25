package astric.server.dao;

import astric.model.dao.FollowingDAO;
import astric.model.domain.User;
import astric.model.service.request.follow.FollowingRequest;
import astric.model.service.response.follow.FollowingResponse;

import java.util.*;

import static astric.server.dao.UserDAOImpl.hardCodedUsers;


public class FollowingDAOImpl implements FollowingDAO {

    private static Map<String, List<User>> followeesByFollower;

    @Override
    public FollowingResponse getFollowing(FollowingRequest request) {
        assert request.getLimit() > 0;
        assert request.getFollowerUsername() != null;
        assert request.getAuthToken().equals("ae04c02a-bc73-4b58-984d-e5038c6f7c02");

        if(followeesByFollower == null){
            followeesByFollower = initializeFollowees();
        }

        List<User> allFollowees = followeesByFollower.get(request.getFollowerUsername());
        List<User> responseFollowees = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if (request.getLimit() > 0) {
            if (allFollowees != null) {
                int followeesIndex = getFolloweesStartingIndex(request.getLastFollowee(), allFollowees);

                for (int limitCounter = 0; followeesIndex < allFollowees.size() && limitCounter < request.getLimit(); followeesIndex++, limitCounter++) {
                    responseFollowees.add(allFollowees.get(followeesIndex));
                }

                hasMorePages = followeesIndex < allFollowees.size();
            }
        }

        return new FollowingResponse(responseFollowees, hasMorePages);

    }

    private Map<String, List<User>> initializeFollowees() {
        Map<String, List<User>> followeesByFollower = new HashMap<>();

        followeesByFollower.put(hardCodedUsers.get(0).getUsername(),
                Arrays.asList(hardCodedUsers.get(1),
                                hardCodedUsers.get(2),
                                hardCodedUsers.get(3),
                                hardCodedUsers.get(4),
                                hardCodedUsers.get(5))
        );
        followeesByFollower.put(hardCodedUsers.get(1).getUsername(),
                Arrays.asList(hardCodedUsers.get(0),
                                hardCodedUsers.get(2),
                                hardCodedUsers.get(3),
                                hardCodedUsers.get(4),
                                hardCodedUsers.get(5))
        );
        followeesByFollower.put(hardCodedUsers.get(2).getUsername(),
                Arrays.asList(hardCodedUsers.get(0),
                                hardCodedUsers.get(1),
                                hardCodedUsers.get(3),
                                hardCodedUsers.get(4),
                                hardCodedUsers.get(5))
        );
        followeesByFollower.put(hardCodedUsers.get(3).getUsername(),
                Arrays.asList(hardCodedUsers.get(0),
                                hardCodedUsers.get(1),
                                hardCodedUsers.get(2),
                                hardCodedUsers.get(4),
                                hardCodedUsers.get(5))
        );
        followeesByFollower.put(hardCodedUsers.get(4).getUsername(),
                Arrays.asList(hardCodedUsers.get(0),
                                hardCodedUsers.get(1),
                                hardCodedUsers.get(3),
                                hardCodedUsers.get(2),
                                hardCodedUsers.get(5))
        );
        followeesByFollower.put(hardCodedUsers.get(5).getUsername(),
                Arrays.asList(hardCodedUsers.get(0),
                                hardCodedUsers.get(1),
                                hardCodedUsers.get(3),
                                hardCodedUsers.get(4),
                                hardCodedUsers.get(2))
        );

        return followeesByFollower;
    }

    /**
     * Determines the index for the first followee in the specified 'allFollowees' list that should
     * be returned in the current request. This will be the index of the next followee after the
     * specified 'lastFollowee'.
     *
     * @param lastFollowee the last followee that was returned in the previous request or null if
     *                     there was no previous request.
     * @param allFollowees the generated list of followees from which we are returning paged results.
     * @return the index of the first followee to be returned.
     */
    private int getFolloweesStartingIndex(User lastFollowee, List<User> allFollowees) {

        int followeesIndex = 0;

        if(lastFollowee != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowees.size(); i++) {
                if(lastFollowee.equals(allFollowees.get(i))) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followeesIndex = i + 1;
                }
            }
        }

        return followeesIndex;
    }

    /**
     * Returns an instance of FollowGenerator that can be used to generate Follow data. This is
     * written as a separate method to allow mocking of the generator.
     *
     * @return the generator.
     */
    FollowGenerator getFollowGenerator() {
        return FollowGenerator.getInstance();
    }

}

