package astric.server.dao;

import astric.model.dao.AuthDAO;
import astric.server.lambda.account.HashUtil;
import astric.model.dao.UserDAO;
import astric.model.domain.User;
import astric.model.service.request.account.LoginRequest;
import astric.model.service.request.account.LogoutRequest;
import astric.model.service.request.account.SignUpRequest;
import astric.model.service.response.account.LoginResponse;
import astric.model.service.response.account.LogoutResponse;
import astric.model.service.response.account.SignUpResponse;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.util.Base64;


import java.io.*;
import java.net.URL;
import java.util.*;

public class UserDAOImpl implements UserDAO {

    AmazonDynamoDB client;
    DynamoDB dynamoDB;
    Table table;
    AuthDAOImpl authDAO;

    public static List<User> hardCodedUsers = Arrays.asList(
            new User("Jared Hasson", "@jaredezz", "assets/images/astric.png", "jaredhasson"),
            new User("Thomas Banks", "@tb", "assets/images/man_profile.png", "tbanks"),
            new User("Wendy Watts", "@wwatts", "assets/images/woman_profile.png", "wendyw"),
            new User("Orville Klaus", "@santa", "assets/images/santa.png", "ovk"),
            new User("Manny Woodpecker", "@birdlover", "assets/images/woodpecker.jpg", "mannywp"),
            new User("Fanny Follower", "@ff", "assets/images/fanny_pack.jpeg", "follo"));


    private List<String> usernames = Arrays.asList("username", "jaredhasson");

    private List<String> handles = Arrays.asList("@user", "@jared");

    private Map<String, String> usernamePasswordMap = new HashMap<String, String>() {{
        put("username", "password");
        put("jaredhasson", "password");
    }};

    public UserDAOImpl() {
        this.client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
        this.dynamoDB = new DynamoDB(client);
        this.table = dynamoDB.getTable("Users");
        this.authDAO = new AuthDAOImpl();
    }

    @Override
    public SignUpResponse signUp(SignUpRequest request) {
        String username = request.getUsername();
        String handle = request.getHandle();

        // return success message, (milestone 4 - add user to database)
        if (userExistsWithUsername(username)) {
            //username exists in user db
            return new SignUpResponse(false, null, "Username already exists.");
        } else if (userExistsWithHandle(handle)) {
            //handle exists in user db
            return new SignUpResponse(false, null, "Handle already exists.");
        } else {
            //hash password
            String hashedPassword = HashUtil.hashPassword(request.getPassword());

            //upload image to S3
            byte[] image = new byte[0];
            String imageUrl = uploadProfileImage(username, image);

            //add sign up action to auth table
            String authToken = authDAO.signUp(username);

            //add user to user table
            User userToAdd = new User(request.getName(), handle, imageUrl, username);
            writeToUserTable(userToAdd, hashedPassword);

            //return auth token
            return new SignUpResponse(true, authToken);
        }
    }

    public String uploadProfileImage(String username, byte[] image) {
        AmazonS3 s3 = AmazonS3ClientBuilder
                .standard()
                .withRegion(Regions.US_EAST_1)
                .build();

//        String fileName = "temporaryFile";
        byte[] imageBytes = Base64.decode("iVBORw0KGgoAAAANSUhEUgAAAKAAAAB6CAYAAAA4alhkAAABimlDQ1BJQ0MgUHJvZmlsZQAAKJGFkb9LAmEYx79Xhg0HGUhENDg0RFRIEbhFCUXgIGpQ0XKe5ymc+nKeqVtDNESL0FZEYH9BRG0RTdFaNPRja2gMpGsIuZ7X0zwr6D2O58OX7/N9n3sO6NqVGNNcADJZQ48szvtWVtd87he44IaIMQiSnGdz4XCILGjVzmPeQ+D1doJnvfab+5sfnoPM+8nNUq9+99vfccSEkpcBwUsckJluEPM7YkWDcT4i9uo0FPEpZ9Xma85xm58anlgkSPxG7NmQVert4vl+OSUliP3E40mtUCJe53rc4VcdnNEKcnM2/lWikl2OUh2idxhJTEJDASX4wKAjR0qaFIX293dfoNEXJCdDmTrSUJGCQf0LjqxoI8lAERJ5FFJC9AT5v7Aja5FGqjBw0dZyxxRvAt2VthbfA863gcGHtjZyCPRtAWdXTNKl780LpsveU3NmwTF9i2HNOHgnn5yesj3iLNDzbFm1UcBdAeoVy/qsWla9SvM8Apflf7N/8Bdg83M0r0JABwAAAGJlWElmTU0AKgAAAAgAAgESAAMAAAABAAEAAIdpAAQAAAABAAAAJgAAAAAAA5KGAAcAAAASAAAAUKACAAQAAAABAAAAoKADAAQAAAABAAAAegAAAABBU0NJSQAAAFNjcmVlbnNob3Rp9X7qAAACP2lUWHRYTUw6Y29tLmFkb2JlLnhtcAAAAAAAPHg6eG1wbWV0YSB4bWxuczp4PSJhZG9iZTpuczptZXRhLyIgeDp4bXB0az0iWE1QIENvcmUgNS40LjAiPgogICA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPgogICAgICA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgICAgICAgICB4bWxuczpleGlmPSJodHRwOi8vbnMuYWRvYmUuY29tL2V4aWYvMS4wLyIKICAgICAgICAgICAgeG1sbnM6dGlmZj0iaHR0cDovL25zLmFkb2JlLmNvbS90aWZmLzEuMC8iPgogICAgICAgICA8ZXhpZjpVc2VyQ29tbWVudD5TY3JlZW5zaG90PC9leGlmOlVzZXJDb21tZW50PgogICAgICAgICA8ZXhpZjpQaXhlbFhEaW1lbnNpb24+MTE5MjwvZXhpZjpQaXhlbFhEaW1lbnNpb24+CiAgICAgICAgIDxleGlmOlBpeGVsWURpbWVuc2lvbj4yMDA5PC9leGlmOlBpeGVsWURpbWVuc2lvbj4KICAgICAgICAgPHRpZmY6T3JpZW50YXRpb24+MTwvdGlmZjpPcmllbnRhdGlvbj4KICAgICAgPC9yZGY6RGVzY3JpcHRpb24+CiAgIDwvcmRmOlJERj4KPC94OnhtcG1ldGE+Cj7d3ygAADAtSURBVHgB7Z0JkFXHlaaziqLYN4FYBGIRi8QqEAi0IqwF7ZbldrQt77a8dffMxMR09GzRERMT09Ez3R0dPR53u90eL+OwZXlTy7JsWbIka7UWoxUEEhIgiVWUQIDYiqWoOd/J+9/KuvVeVQEPCln3wH25nTwn8+T/Tua9dTNfXQihwa4Wu+rtarWrzi6IuPII03zxkX8ku5BDnDLi0CG7iJPXKwtVF53Ee9tVTT864SnqRw95qX7kV9NPfmf6D2ey1DZLut7O9FNW6m8b/2Oyv4xIqEtgIM2A6LKoDyL5EIOqOoTUUxkDQ7100NPBVWPhUz3iqRzi0q2QetLRmX7VTfWL30R4u0in+qnDpfZIhnQTFvVbltdJ6ylP/Kpf6m8bu3z8MZw8YJ6JBY0wGGWVBoJ8DSAGhkeXRb2MUIPAoMoTkgel/JRX0w+vBlEhvMQFdHhSebQt5U31Uwal/NJPiExI/ScuWQrTtpIHpfJK/W02w1ZV7Y/RAIZIRsSADIQMSVwDAy98qeHhU13yiUOqo4ElndbTwCuPcupKXqpfMq04bxtx8Sue6ledVD8yxVvUD7/kKUSe2uUVEx7S4lO81B9tInsQVrU/6BRIYJSxiUOUyduQxthQpYGLJW3TlEBEHXlS5YmXkHIudKWDZ0nPUz4hF1RJPzKoT3uJp7pS/VbUjqRf9QlF0ql2qazU3xEXst9R2V8Dg4FFimswhV7KiVNHwIVHg2TRHMAMfspDPUgAogxKQSK95NMZySBEJ+XSb1GXRR68kqu06qKHOBdEGp6ifvK4IOSl+qWTcsUpl4xq+mkz7Sr1d2J/jARpoDRAGBXi7lB5GJM7VkiDlQ48+RpoDaBC9BBXPULpQIb4NGCE6GXAicNPyAWpLjwpST58tEU6pT/NkwxC8StUXlE/+bosmrdHeiUfHsUpK/W3jTG2wSaEPsgYR48hyINkQAGOPA2+4hKiQSMEEIRcyFDIYBAXUZZ6E+knXxd10E8aEthIE0/1CyhF/ZIFb6qfOPLFT/1UvspT/eRJnvSTJ17yJI9QvJSX+qvYX4OJkQAiz+0wHkSZCAOSr4GScfUtpy5lELIUJ5+6kOoQ14CQJ/BJP/UhDRxxgQW55FNPeoirbRb1MuknX/pVRog8yUG/ZKgt8JCnupX0U44e1ZVO5VuRt0sySFMGlfoz+2OQRrtkGBmPgVMcYBDHkIRcGijiGkgNhtJW1K6ejC5QkJY8QskXH3nIhB+Z5EPkSz9pyqBq8gQAyYWfPPFTN9WPTvVB+invSr/6D28qr9RvBjGqaH8MxTSjQYRRA8TAyztoQDBmMa48hVKELAgdIg0M6RTkqX7xk4d+KC1XHH2Sl4boF8ipWyTqQcihnoBDHiRZlfSrj/B1pp+60gNvSsov9ZutGSg8nIwh4xNCKiOO4TSwlFPGYJOfeknSgJoQPi7qoQNSXcBFPaiafn05qAMhB5IM9ENF/aSlH9nUo44uygGe9KflqmfF+Zcz1a9y6pb6sdJx2B/DMsgMAIYVSJSmXAbH2BpY8uBJKQURvJVkwE8+5ZJBnLqpfqWl34q9jmSmvMQheLmgVD9peCQLGYCPPMVTfZadt4e45Eum2k4dSOVF/ZIpnlJ/e5u7/TFKP7sIRTI0aZgwZGpMpeGjHqAsrslUz4qcJFNyVE59XeRB8MIHpXzElaZcUycg0lKBctKSQRqCl3x0SY70Wla7fNUlX/Fivc70U6Z6pX6s2In9AQ/GZYDwhOm0iBEZNBkbXgijnmvXdLsus2uRXcPtQo5IA0BaA009AYE4cgmlX0BGJwQvPFy0S/nIpg51IfLhPRb9kkX9Un97O58U+2N0gUWDYFk+mCngAIEGfanFl0yZOvXC+QsWNEydMmXwgIEDBlHc2toa6urqnfGI4aOOf3Wtlo9IIy+vC0csrK+LeGnNpBKQH3OdO34Yn7MgxOJRRxtXpTLyIrKq6TdUd6pf3wsTlOo3K7QeoY89pd/6f+RIol+2sd7mtumO/Y+1/7XXjyX72yUvQoj1uQCgj2+SnmXxj1188cULFixaeP727dvDu7veDfv2Nxt45ISywSPoQJYpQ1Em6R34qhcVqxTT7UVlA5RlOm9WwUyZtSWtEfO8yLJjF/hs09IWi/WK6VQa9dJyj2cZ3ddflKh2VdOv9nYsPxX1pyBTT/F2EE4EQAqU5M+eMmXKBXPPO2/e2rXrwt59ey0rozhabnBMxL+6elyG/XfPRo4Rnowg/wAHZhrLJ484/Ayey4AvAa17NvHEanDkpCHPZVlJ1J95RStArvtab5cxpPozfgTGVlhEns+iPaPfbIHndf3RA7uN3A6WmRA8sYeZLUll/fS2H1P/T5z+dJplDUgbIUAHKc1YcF02c/bs3hs3bw77UvDBSc8zEPmAAiCARC2KYlDx0/myEsUjHLN6yDGSDBk0y85qxiDW88YYv9Ww/0pFGaQE0yizTu22kjb9kmehTXvUUStOvn5ppk3YNfagrf+Uq5eEkWrX/xOnXx5QXq+t7XFsVE7IKCwYPXr0kDfWv+nrNXU6sJDL+22NjeMVJUgioa0Hs1FMc7uMZw7K+aKZK4gRkzNkA5KNUDSfqbey9gDDq9iatIsWxHVslNJz+tGcW7yjGWXbOuuNL6yNOet4bfp/YvTLAyKdccDzCYwpjHSDcnp9r/qBhw7Zn4ytV61WJfoSOm01fYRNlA38SAsWN/YK55vEaeQbvWr5y+x+9tFDh0NThlj3Ju3AY7y5MaMsYDJySEtYPL05nH/WwTBt1MEob2tjWLauT3h0VWNo2mWKEv0uw7nsQyOAzgyULUNDaD63JTRPOxIOnUH3WkPvzQ2h7+r60Hd5fei1k3a06Y9tsh6bLN2E5G1HQSwwMZkO+iDKo236Xba1x2+8XI/Z0ELvurG12RM/ZhmZTaQG0XSFqs58BB67cv3kZ3QK6weAAmE67dItpmSaTr7AWNfSciS0tDBgkTLzZONq7GaVmQ29wi0NdeESu1xENugLDdoLG+vCufW9wu2HWsNKkwX5VBEjniLqlNWbOfZguOWiveGSqc2xRVnxwgnNYeHE5jB3fN/ww98NCCs3Zk3O1NqI2L+8hdY0ulMXDk1sCXuWHAn7Z1g/yOKy/JYpBsipLaHf5F5h4EP1ofcbdD32Kfcq5GTtolYEYSbC842/C/1RJrUjxRbGqdWb4tnoJZKFbQWYOCfivm521myYcv00pWP/c5mZlJ7UD/hAk8BH07nkBZVmZCH7wlonrddtNsBwbgXvF57vFpN6CZLJ5xIRtfIIzBC+aqKa+EonLLDKeZA9asiRCL4pBj7sC39K9XXh4snNruar9w4yTxgfQ7T6NGTtpAdZG4geHnok7F5iQJtuwlpMFjIlEgbr+f5zAGZrGHRnXWjAE8LiU5tHI75cbkznXczyPKiiX5aLwIj10R+bwCdeMIYRGPCk/ci+VO7tYv32+vGXxu/6aTfVo/TYPMXVEhffY/oBGuCjbRBxoBPb2lYWW21pB1vWuToHjxUZN50GHJeZ97ukwcRSQ5bxuKUh8uw/PJf1Fs5jkT4xDVUgpt3c8wmsDkQrhCnLg+cy44WiWvTEK3aOQWkNB+YcCc0zTAD1XA5hekWZ+40HXm+Lf+mQbJKicNcdgUJ+NJjLsXhn+p3XGExjVomIp0y6WkpOzIv6La4KLpx2WHWPRz6vST+Qlo2PN8R4JNXHzsrzm65YQA2q9Yh+ARD9AiKtoWlKEweUpFmy2LjZwFiEZQehx+m8pc+Hkw7BmEVjxD6zrFjWauvD6D1dhhXK2FQlDt/5k229Rz3ke0g8uZIyeNUel+HtoyrtjdWbz7bKuRzTQpIyrhyQsa0HjJd8OhbbCCCRZ/Xsgl363DBJ2uVV0E+/Yj37tD4xoziv50e5MY0w6c/0GI/XN4Y2/ZTZiFCpnX7jeQ/oBy6agq25TtiHKZc0Fz1TWYxggJTBF8DRKGfblOiFVt4WRuPERXJW0bLgxZBROPUSPgbHss4ezTuyGVGcsXiO0lFA5HUZZES5EqmKh8cyvaIrkSUmTWtZ2SHn7ZWppH8YIsqGBfbs3sAtRF5krq4/eh/K+R6p74W2IjjrP0pdLqJdf1vN9vozPjEbm7ciT8fIqaYfAOIFAR1x2kyoZuP1RPCZvbMiC3JPn41CNKvYux+6RIGAaoqrFd0XFVuXtSeKsVb5SByFkIQ1NkENYaEh0BSaaSy5mk70S5KrsArxCxjvhqMEK/GGG6cM3K496I+a1D+X2U5/lKHytoaZyEQW+T2tX4swgKbpmBAPqClY+TSd9sYpKEu467fM+C+E1UyV2AfKQ4vwdW2XF3m9HvV1ISmLWySs3qL7n0yeZEpWkobX6/ocR2PzBnuCsoaN1i3q6HI5lkjbl5XBSx0Epe3UFExZnPrU5qjPp/JYLdbHJsjIZMW4+T+8nOmPNoQlNhw+WCMfdfGVsT75Ub8XW1x1mK4zHtcXy13nKawfcOliKOiNiGHADAojEqzDvvjlrwOUElALY1rk6UPM6IUBtRwnJEE+2HXh6YPGmxknFtin0hjTkk+vMYdMPaZ2D4knV1LmvIyQtcNr0zANinul1tD4itXN5ZgvMVk+lSHe5VqE0ILG1RZm7fB20SJLe//RA1FO/wkzfZ3pp1oEYqyj/sbemofN+68+UGKNIT/XH/sV0/aZ1Ylysz47b8f+n2r6NZTWQ1rsprfAh4iQ6ViXl7faaGFvONxoNrCtfrGeC+GhAy3hMUCIxKJX8XpWYP/heejg4bblmA9gHIbM1D5QD61sDI+92ifKK4IQHVkePPDGunFtycDlsmyQiPd+IYQ+K60i9XLAKd2WB0/j89S3f7QNWR4aFuiHkQ84of+LBuxKv/OamrxdinsGpSqLBc7vXyjLt0LKefxPjLSH/qm+ZqHa7GXGlfX/VNMPuES68SBNDxkeiG4Sj333b5u6HTMpcK9g1bYeaQk/aKZ6Q7i0dzbduaFgsss+AN8Pmu2vIfYgmsF0RVYWDZqxwWrUZM/hfvBYX49fOu2AFboQT/uHJQEfPPBiYHHE+03zcmRkwnvtaA39H6RmXTgw0wL1LGa5csAHT73xipAluUTUVji8/xlj+zi9a6/fuY1JLUWOy7UPTEt51BOBlIm1XOm3Uvsf9dNbaqiOS8/qEz/19bPOG2AXQKQvAh2hwAkPacIvz5t/XuO2t9+2qEhLYtKIqHNgrTjcEposyTp6oHkbntC9YIC7w4B3e/OhsM7KITe2QJW3gIiRBTzl32oPl1esbwhb37VmmKyB/VpD8+G68MKG3uGOZX3D7Y/3CeuarHk+mtnomFeOLXMNUR7aTGa9AbX32tbQa5el8YT97f1E+xNh79frQ7/f1YV+Br5eW6Tfq3g73ePRVqHPovSAwFvcTf18I+Q9keACCBBCe0Te/1S/Fbj+zAfn+iMQM/PnFaKkRB6K0HEK6ad1Z9hFjwAYaYWKW5aDkfxln7318/1Wr14dDU6JCKQx6BZ6H+2T6ZrBQlB+Q2cRfbezKh1lSWYhdDmWp7BQ7AVRv3RKv2mkkpEPslpoeaTj8CVAspy2VlIr9oh2x1jMoaQdubzIhbq2/h+v/qhFkl12O8VZIu8Ppkjtf+rqx8sJfAAMTycbp0Akj9fiudWK316hxzJzYg7xEaX7RpmnSOOI97SXR2V5/Q6R9qZWvRi2L/OqFFTUbwVeCd3Ug2KeZ2fptnhsZdRALjFuCtpyYl5aI4qsvX5vnH9EbYn9vFOxTd4eNdVC583tn2dkfUBczHO+THpbPO2/F2YcBLXVDwABGrrpCfMieQKi1oWUcTnFhtpn3mJFrHGtqgqrVcEIqulxS5BWFZdY7aONKa+CDKapzLixJnyRo6jfH7ziiVHobPbhnpma7T1dJoECJ9hFeVmP6E+8czv9Wb+8kbS2o/1r0/8Tpx+w0QtQAwmQxAFmXMW25dfrGVjadZ/W4ofhIqtiDPHGxKSY0dxhmirWPs5KtvNgtkjITMEpHdG0CW7M06pMVdo8D/qNDGRufOK5fo/m7fJpmTJvQSYxB2dbUyKH6TQWb2uP6K/U/2g5Wt7W/8yallnb/p8Y/QAOog/ejyQkX9MycaeVy1eEZ599VskyLC1wXBbQdCvwSZg8oqZnecrDc89bEPoNHCy+MiwtcFwWELAAHCCURyTNpTWhRd0bAsiSSgvUzAICHkADgCwgCBXn5CxApzyLllRaoHYWAIB6DIP3SwGo6RdtAFC8pEsqLVATCwA6gMYFEDUFC3AAUuUWze+WiZdUWuC4LYBXEwE6CBAKgKnXI57d48NWUmmB47cAoBLYCLmyB2kONh5Epw+j444fyyyptEAtLKApV7IAIFMu+ZW8o/jK8H1sgTHjxoedO7aF/Xv3HbUVps6YFV5b9VK4/iO3eF2AxgXo5AHTaVY3JSqXd/TKnX1IgXia7SiP115eGda/vlZZ3QqnTpkcrl56Vdi4cWP4+S9+2a06x8p04/XXhd69e4d//fldYeKECWHBefPCzl27wgO/fehYRR5zvZPZ76Nt5DvbmsLwkaMNgG8cVVXAN23GbAegKgI+gMdjGB65ADCmXK33BE7yKU+9oiW7T337Dwiz5y8MjX37hjUGxO7SacOGBa6DB+xdwBNMM6afExobGx2Aw08bFmbOnBG2NjX1CACL/b71c58J48aObWeB/fv3h7/9+39ol1dM3HzTjWHwoMHh4UcfC2+uX18sPqb0gebm0HL4UBgwcFDYu2d3t2QIfEVmAAa48HCEpCFNxYQQwCNuf8pNHSRFR0dnz5wTuKrRr352e7uip5c9E3bv3h02bNrULv8PPVHsd5/GPv539Le2bs27fqAbX8px48b5F3jQ88/n9WoR2WHvhI4cOy7s27ubP7V3StXARyUAB7gAICTACZQCHvlctgFTLLCfeDp/wfzwgcsWuyf63vdvC9fYdDx16pQwZMiQcKD5QFizdk248667wwULzw+LL70kNJlhBg8aFAYPHhz+4atfC3NmzwoL5s83/sEBj7F23es2ld/tDV965RXu5fqZV96wYWPFznCQ5Ze+8PkwYvjwsGPnzvD73y8Lzz5v7/VXILUBrznAPP7QoUP8y/PEk0/51F6pfbPMyy48f4H3p9kAxVLjRz/5WSj2u0+fxnDg4MHwzW99p4Lmylm3fvbTYYjZAcJuZ02aGH7xy3vCH33opjB58lmhsU+fsMuWGK+8strbSfsqtb1Sfw8eOhia9+8LgwYPtTMid7qOah+s+bgqEQAsIgowAjyBkDQ8hFwnlRptTdavX7/Qt0/fwLpovq3LOJtmy+YtYdSokQaw2Q6qhoYG55swfnw4aAO1Z8/ecOaZ48ISA2+vXr3CJjtSDhABSDwqnoSBp2ynAWvcuLE+/R7xo9jaujhixIiwd+/e0GL5I08/PcybO7cqANM27Nu3L+ChTjvttPCBJZeFp57+fcX2Xf6BJQ7Obdu22WAODmdPmxY+8uEPhS1b3sr7TWtYmx6xN8q/8qUvhKH25dtrNwBPL1sWfm8zRDU6ePCQnSx3xPuITQ7ZoVCLrM+zZs30Pq1bty5gL2z6nH2psDPpYtsrARCdO7dvC2POHB92v/uueUHgUpmK9wPiemXFi+4BARdOlDWenKlAR1p5HsZXlyy3BwjD3Hvfb8Iu6zDAvOiiC8PYM87wC1BBeLkf/PD2sNkG8Lprljqo8G6rVr3sgAVA4888M/Tv398HZstbb7lXmXvunHDTjTd06NV+O/31m9/+ThgzenS45aN/HEaMGB7OGDPagH1ZznvY1kM/+dm/5mmAh7c+3HI4fPHznwsDBgwII0eO9PJK7ePL8a3v/L9w/vzzwnXXXuNrPQCYEmtTviwcCsqN0SiTd4WB97U1awIevFJ7vm92+Ld/9ic+Bf/24UfCSytX+ZcBuYdtSwRfwhUrVoZ6O0iUfkGV2j5n1qyw/KWOHuzw4cNh7+49YYit0Xe+s93rd/djW9PWsK5lQL7mA3ACGjKIc+nGQ2tDe8u86DBhPznEt37u3HPd+HgEUdomBgfwQUOHDvMQT8glGjhoYGAKgd7Z/o6HL7y4PFx79dKAF0uJAd9tRt69e415kEMOAgCFNxbhXVJ6174gb5tHg/iy4FkGDOAkZPMaSfuG2cBBO3bs8HDZs8+Fa6wNyC/SCgMAHu3X9gWE/vTLXwynm0eeOX26T5udtSeVxRJi+tlnW90R4dKLL/bZhC/oWvOGUKW2w1uNdtv0O3rcmUcFQOpseH1NqBsxMwcg8vGhAE7ejzx5R90l9xz6rDGXXnKxewc8BoA5d87sDneG6ddo79499CG8+uprYdkz8R3G3o29Q7OtHakLDbL1IjR50iSf5rpzk7XNzsZ+/HdPeD0+0uPqSAOgPra+wpuwFoTwpE7J11xee+DAgV40/Zyz3RvxRUtp7Blj/Eu3e0/sD2V4MIgvX1ftga+hV/xi7bVZ5Ov/8k1bF58XJk2cGCafNSlMnDjB15fwVWo7a99qNGjI0ACguktNWzaFZ9/cFuqmXu7golVcAIt1H+YBiKQBIwQgIfKyzR6ePukfA2zahPra4E6aODGMHjWq0za8+eYGXyNiYLwU67EzbDDXrF0bVq9+1deDeMbPf+bTfsPAYHYHgDt27AwPPvRwVd0M4q2f/YwZ0454w9ua7nfeiZ42rfTGG+vDnDlzzDufGT718VvCcFujQqwHU9pk693Btu6z02nDZz/1SVuPtlh8lIHwcHhp1SrzoNXb0wzwzdFedOEF3hZswJfvbbtZW/f6G+5V+bK0mCyoUtuZ5isRs8UA69/GN96oVFwxb+3ql0Pdoo+HuoHR+wO6qDmyR5C1gVFCNC9pc6ryT2r4/Isv+nTIQDHlMF10pDYX88Ly5eEZe3ubbQQsvMfY2m3r1qbw2ONPhGeeez6stMFjEAEhd5isMTtQmzgv6h5Ad9hA9veblmZ7ZsYNSNs03SaQdRVT4iHTfZZ5ooEDB9jNxxa7U/1Vh2Y88sij5rmbw4QJ4+1udpLLe/Sxxx18HZiTjFUvvxzetfWxr13ty4c9eFLA9H3BooW2Fu4XVr/6amAtDLEcKLadJUglGjp8RNhlX6zObkCK9fbbnXNdv+jxB217zr2fPByWAZDyiKQ1Hctq9ekUVxRe6/S4sWe4sZG7zxq+2qZSLu7U3tnxjoMx1fn4E0+mSY//+r77bd10v91dTvWpanu25qPwjjvvsimyv3nGYWHDxvbPGbnzK979/fXf/F0H+ZUymOL/zz/9s3vp1xPvUKl9v3ngwcA1xR6LbNu23deIxX6jg/Uh18wZ030dunzFS5VUd8j7nT0C4qKfTL/QN775LfO2p4XRdiOzxh5LsVS4xG7ooGpt98Lko7F3Y+jbr3/Y3tT+ZilhqRhttOeZB/fsCHVDRoZbBn3dwQboIAGRKZi4wIdXJA6flQmLluqEig+UO2GtWjRl8mT3DNw5rnip7a8nx/JEH+BWIgZFA1Op/HjyUvB1JWeN/eyFqFq/KV9pd/PHQsU+8kVMv4xFmV21fZh50B3b3u7yIXRR7tTps8Jzz94dWifOC33HH3AAAjhNsfCT5hYT4IE20pqas6gFJ4FYe+zcuSswlb4XCJDvsWeGPDc8HuqJfh9N2/vYY59eDb27/We41BajzhgblgwdGn57zy9Cy5i4ouN2EIABOk3BhFzciMgDAtKf3vrFL/djzVDS+9cCx/M2TGq1s0bvzT0fYBPQ0pA4ZZqeuzf/plrK+B+cBbZsXF+TPq17a4CDC5BxQTxcEsjIiw+b4jScTtPwllRa4LgtIM+HINZ6pAVAQt2MEKrcoiWVFqiNBQAcBNi0BtR0KzCmgASEJZUWqJkFBDJAp+lXgCNPUzNhTCunZk0oBb2fLQAABT6gBfgIuQCk4EYI70l9EG36SvoDtwA3FkyrgFBgVJdJC4D5zUjzvj1h3+5d4inD0gLHZQEACNA07QI01oIQwCQOOAXQ1n4DBoX+g4ZYVm0IMNdSXm1aVUrpjgVOy16e6A5vNR49WhEIBUg8n6Zm6sob1nfnj/HVlJX5pQWKFhDwyAd06V0uXlHej/JImpSVLsPSAsdhAQDIBWkaVggYBUoB1X7Yw3nLj9ICNbGAwIenA2yakhXnXUF4ACMhfCWVFqiZBQRA/SSlpmAAqBsS4pDCmCo/SwvUwAJ4PICmmw8ASFzrP03DAp8Aaiw9T8VzRtIW1eJ9xFReGa+dBcafNSWsX7cmXHrldT6t4gW5UtChjTwBMAUlZT1One227/HGlQ2oagHAN+GsaXm5gKdbC4EQhtTryfOJLxfQE5FTAXzsLb7e9vB2l9hANXvmzO6y/0HyFcFHJ/FsAE1gI2RKVsgLqemNR5fnA37y4x/zvavp696fvOVj4ZHHHuuw78JkHzXVAnxswfzkJ+LxYDSAzT5r1qwNd9ipWN2lSbbTjlMWnnjqqfCJj30039FG/V273g3/+2v/6KLYxH7zTR8Mw2w3Gu+v33jDdb6l9J577/ONVR+3uv/r7/7e92VI93/+iz8Pj9hhQk1Nb3s7v/q1f/K9IiqvZTjE2nXAds5xzEZK7Pfo06+vbzpK8481Xgl8yNLUimeTd8Mrkk8IKV9hzC18zrSTpdheeKYdhnPhokW+2wwWNsOwk6v5/tqcbtXZOSOFJlVNaiP7t77zXdvg02DHbcyxrYpz/PiOp2yXWnfoEduRxgWx0eZBO8JN2xcP29EhohtvuN73G99p4GY/xw3XXet7cjdt2hz2N+93tgY78SC1Du3TJTknKuSkq5H2mnzT5k05CAGf8mqllzUfV5EAmh42C3BMt1r7YUnydWkqtqz2NMr2qU6znWe9bK8oB9/oLyacTcKREDopoH2to091ds7IG7bbvqV3/1B/sHt7MtgAzkkF6zdssA3aZ/kRH5wXc9EFF/ixGpzB8lf/828cNOfYpvE+djzGO7Zt8Ze/use3XC5efKkfgMTBQez7HT1qtJ3KsCXf7IOnZeq9486f55uJfnnPr/2kBHbibdocAXi0VqCNF9qWSja7j7TzcdiD8tRTT9vOusk+d33/tnjC2Kc/+XHfwP7DH/24qopm25TFZnEBjqnv9AIgq1Y+igJuOCoRAIR43tfHLgCmfSDE5QkBIkAVSC3ann770COBi03ed9sA1Qpw7bVUTumckfqLbwkNIydFn/3ifeHI6o7bNCtJYEskZ8Wwf7aXnZPCSVoc1nOX7UM+b97cwLkxbG1cZ1sYr7XzZq69+mrfKMV5LWw8J+RMlzr7yXXAyKkNAG3SpIl+Vg1fQAgwwvu0eVl2+g0bNtTzj/aDNg61jT1vbV0d7rn3XjsV7FI/NQK5F16wyE9lwKtybg57h7uiFIScPJB6w67qHk8558kAMIAGuAAZLx9oqiUPUrnH5dm8pMIHHeZQxLvu/qWdWfJ2+MynPuGnEHR3aqsgstOs9JwR294fWg82h7rGvjbaU0LoAoC0DdAAIr4wHN/B8Rgck8aXCJp/3nl+etZDdrgP9JJtD73k4os8zgde0usue8b37V591ZV+tBpr3oaGXvlMAO9NH7whDLc1V72Bg2PYOLbtWIkN9T/+6R1e/b777w9/9pUv+xkxzDgcE8cUfsh4+OJ0h/j9PsDHr/AJAN2pd6w8HGz51qb1DkC8rsAmefJ4lEFqU5fnA1515eXWkzrbSB6nFxb4l122ODxvXoEN0LWk4jkjLvug6QWARv/jUy+FZ147Ldz11BmeLn6w15gBA0DpfttWyxPhbThZQcQ5MFpDkseJB5x8gMeEuClZZACYNGGinUi6wY6AO9830rOX+V/+77ed50++9AU/Xo2pky80JzO8YkeFQHhFljH5WTKe2/EjdQS0gTSnZ220gzw5OYE2AvLuEGs+Tbv8gKOm4+KNSXdkdYfnnW1bw/Itu8KB8RflUyoW1pWu98jT1FsJqB30MTU89vjv8lML+JZScdoU80g1Jj9n5NyrQt3IiW1XdubIsJbNYfaEXeGjl26oqhXQcHRtCr4iMwM6zPrEedF4yhl2GhXrRlF/O3yIxzGsyaBLLrrIb8BWv/ZaePmV1X6o5Q3XXRMmTZzo5RyKydEib9iJCZz5wgFFnNuCJ+WG7dqlS8PhQ4fsC/ui86cfHzIPyroS4nQwzrSGrrriCj+5i3689NIqP4Zu5MjTw/LlK7y8s4/0hoNjM9LpmLITQRveXBeap14Sjgwb5x4QHXzlWfulIAR45HOBIcrswwOiFYm1DcbWukeHQG60u6xaU/GckSUNPwt9esW/Ki6aHu+4+vdhedueUu/RvqRjiqNyx48/Myy1qZWj0zj4knUXXzSIEwRW2cBfcfkH/IRWvBCHRsrb32UHq3/wxuvDJ+zx1BHznuh+1s6l4ZgNiEOOrrri8vBv/vQrnsabPWznwEBpO3nuOMueI3LjtMdOyYKPG6e//C//yQHP+g/ivJklSxaHVvtJ2VX2BeiK2GReXPMJhJSdCC/InXdr3wHeNNaAEF5PyCIEdJBuSOAjv8vzATEAj2FY2DO9cfzZK3gCO8Gp1lQ8Z2TprDc7qGjaFafjtGDt66+H//5Xf51m5fEnbTrlSumnd9zpC/sRdp4KXgtirYengnh+ONzWfGeOHeePYtJjMLjD/sevf8MPtRx+2vCwYmXbESPU5YwXrikGpobeDflUTFnaTrwuRw4jG2/LUoBnjZzm/8abbf32myLzjnjf7tAuO2OnEgFCrhNBvRsaQ/2+XeHIwOHuAQU8QKc3oFO95OsuuS4tqBTnTnijHfTDuXM8Y+PwwxdXdD0VVJLVVV7xnBH43363T3hrR187gLI+7NzXGB5ZcXpXYrpVjkcT+DhbjyPO0kHu6qwVDs3UwZmVFK7JDoisVKY8AZsvts4HTMHHAUx/dPOHfFqXF1XdUynkofS7L/82HBwz3QGoGxB5Qd2AyOuRL+ryFolFNAdsa50zceJ4P+7rCXtOVWsqnjOC/NMHHwjrm/qH/Qd6ha/9ovbrTnQ8Y9MnV08RywKuIvHFv+32H9fs5xiK8muVHn76qLDIfjpi2eMPOgDxbunjF/Tg6fB8AqXA2OX5gB+2E9hZTOMx+toagrPlLrXT1znqTOsiFBwrVXvL5e7f21nRw/eHMac12ymjIdzzzJhjVfGerYeH3Fuj3wKptREeeyA+1irKBVjycEzFAE8eEQBSDgmMXW7L5CR5nvzzpy17EuB/F55th0OePXVqxYOuo/jj/+Sckf/43TnHL6iUcFItAPjk5QREwAYprXjmFbVkdJ4OHzy/YmEsmmtA5CjXTVs2K6sMSwvkFsDD6S8gZIIu0uQDONICpEUhsqsTp7nzIJaFMo8Rppnne/W1NfnfR6vXLEvejxYQ0HTjgQ30/E95eEJASH5V9HFsLk/7ea61wZ7A8zSeh6UsjCs9VDVZJb3HLXB44Z8fdw8AIF5O4AJsgIx0cQqGj6sD8es7PKS97Yc/soeuN/jB12LiccX19pcAnluts+dv+vulysvw/W0BeUCsALjwepp6yZMXJOROuSLxVw/+AsCzrF/9+tf2mxTg116vsbtgnnpD/LbbZfb6Eg9Ki6eul0d9uInecx+Da9DiFIBMs/KIiCYN6OQVAWHqFS0Zidt/PefjlPXrrr3afx6qzp6H7LG/dT5qfxumnB88KYIPCa11EbCZuPdNMOWsifZnvFXv2f6++f0vHHfbAZSmVsBHWnkKdRMCn+IWrUz83BU3IPym21133e2/SXHl5R9w5vQvB5Vrl7nvNwsIZIR4OACmdV7qlpiW4bF3xe2zE+IvIY/ZO4E8eObvnj/80U98ep5h79mVVFqgaAFABdAAn9Z+ghh/IVGeRTPvKHiSU4F4DniW/WFdNH/eXP/9sy32y4wllRYoWoBpF/ARAjbAKIgBThE8UJdTMM8B+QkofkqL39jlx6P5WYcT8TZMbFL5eSpa4Dx7k3yrOZ1N9j5lNZpi74hq3Sdg4fV4BQsSMAGlPGXXr2PZq0W8xDlp4kR/DsgzQfZIlHR0FhgzZkxYsGBBePDBB8OFF17om56ef/75XMhNN93kPzj4xBNP5HlphB2Ki+y1uJfMIbzyyitp0QmJF/VNtPFn20BnABw7dqwDkAYBMDyfwEaepmbi3A3jIbs8H/CLt34u3PWLu8O9v7mfej1Cp58+MnB4Ir8C2WQ/jPyW7VR7r9EZtqGImzmIl1/5dfOUutq2Odl2yPFy8MkAH+06Fn2rV692AGrKRQ49Jg3YBEhNy5GPkk6Inxrllyn1I3idsJ6wotHmPTD+e5FmzJjha2j+fs4vmV9hr9sTzrS3oXm5d3k3fraMX8Lkdbi19rO0ouuvvz68bn8I0GOfG2+80cH5mm0dmGB/uz/bfsRav1vMFtOHH35YVcMNN9zg3nbEiBG+pXSXvRx7v22EElXSR9ko2wV48803Oxu/AvqU7ZeBmHqn29YGSGs/4gKiQqZgPB+gJCSf8wH7WFiV2N7I9PEX/2FK/g2G+Qf2VxLeDj4ZtN7eEN5lP6I8e865J0NdTXUw+DzUZ5Desp9Q5fd7mcrYYFTpN4crKQdM/Lr7yuTta2aD9FfmeVVOabwXgH/hhRdct37JXbKpy3SJcwHE/FEhpUr6KKft8A8ePDiMswMLRo4caTNSk8vBMwN8AAgxBePblFZca0JASBnesFO63356tMF+yK5IW03xySLA914lQAcxqAzSwoULHXgrCm+VMz3z580iAQ48EoAFhN0hwAf5n0sLU73q4/UeffRRJfOwM318YQA19OEPf9hBDAB32h8kuFhmCHC0FM8G0AAjRO/I62LShTXSRbYpmg08bJx5zhbMeoVd5WXYtQUWL17sa1cANmvWrNwDzp07Nx9MpLDEYMtrkc455xx/C+lo1n54qan21hJgR88Gm6mee679G98AphJ1pi/91VBeRsYTFgkAAjRCgAcAIaZbvF0KSPJVTrwdXbOUDdkLbOqzU+8n9Pc9If9sP4xci7eg2yn6A0+w3uvXr1/+RjmvtDEFp3uRMUG6BpNJmCpHjx7t0xzbPVMC0PJ03KGmxDqQizvZadOmBcpJpzIqedPO9CGffoiY7lNAKh/Q6QJcAh3leD7yCAEooJR3tGh7Yh3xqjWaXwr/9ne/59/ceXakRU8Qgyhj86cbxXuiLUerk8U/oGMKZfslF3npIxg8yVVXXeWPWVL53MCwfuTuskh4zNPsVAam5yIA582b5+szpn9AAljTLaFFWUp3pg8e1pKs83gmCAC5ESmSgKUbD0LyIIBHGg8pkIrPstpTX/v2rbeTACBOGsATFher7WucuNRkW8CzYRwaaXdiI8zoK5Z33Oh94lpw7JIBCJ6F52fz5893Oxal8YXi0UwRJNwobN++3a9iHcDFX6iWLFmSy1R97la56dG6Et0APyXxpnmd6YOPOufbyRB4b25g9ExwqW2+50vkj5KMb55d3DXg/QQ0QMjVmORTftutX/pyn9XZMRKWzunP//2/8+MpeAgNXbP0Ktuvut72ucZv42tr1ua8aYRXscq3YVKLHFuc9SLrsSeffDIf6KIkHrMw8Js3by4W+dQN8N9M9hh3YEoyuqMPdpYEAJG/ilSi1APKuwFCgZE85SusJMfzFtvut5Rw+ZwuBX3v+7e120Cd8pXx47cAoOKusxK4JF1TutJpqLvvNK+zeHf0Ub8ruQBQYAN4EOs+LtJMv5QzHZMmvyJ9w2446ntJREeWSu8BduQqc47VAt19Rnis8ov1aqUPAEI87yMOwJiOdQNCXECkvCrCtGvfeEoqLdBtC8irATh5O021eD4IUFIO2UllKo4Z5WdpgeOxAAAEXAIbskAYYIQEPKGuy/MBY7Xys7RA9yygKRWA6SKPCxCSp3QRqFbUkc6dPdvugK/MCzjP7oN2UHdJpQUqWUAAZJpljZeCEK9IfnoZAyzV6ZxzpoXZ9khANMVeTOXnDPi5gpJKCxQtAOgggCjwEQI6iOeAeL4cnMU/CcGUEvt+Oe1TdN/9D/ih3Ompoiorw9ICAh6W0E1Iuh4kHzBylwyv1oQWrU5FsCn93/7yv1avVOMSHrj2s1fDSjq1LYBnE+AERgFRXo98UdXzAb/ypS/4sWxiLIY//unPilknJM2fqMaNO9MP+uaO/cCB5sD7ge/VF1RPiJFOIaGADO/G8750cYenw/MJlAJj1fMBOf+4peWIHfhdeaM1ZyufHKrztzia3m4K/H16rIGRvwVvOEXPzTs5Njl1tQAseTgAmN7pAkDKIYGx6vmA993/YPjYH3/E3saot/0gD8RaPfC5c+cOe9lxh2vebyc2jLD9IX37tr0W1ANNKlV2YgHAJy8nIAI2SGnFM6+YOkrn8w9OxnrgQTv392D33sJtq3niYqwDefV8n71iVNKpaQE8HGs+pmAIdJEmH8CRFiAtCpFdmTgNoTOqdjJ9Z3WOtYyXIZl+Wftt2rTxWMWU9U6wBf4/v0aTwe0Y3CAAAAAASUVORK5CYII=");

        InputStream inputStream = new ByteArrayInputStream(imageBytes);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageBytes.length);
        metadata.setContentType("image/png");
//        try {
//            OutputStream os = new FileOutputStream(new File(fileName));
//            os.write(imageBytes);
//            os.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        String uploadFilePath = "profiles/"+username+".png";
        String bucketname = "hasson340";
        s3.putObject(bucketname, uploadFilePath, inputStream, metadata);

        URL result = s3.getUrl(bucketname, uploadFilePath);
        System.out.println(result.toString());
        return result.toString();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        String givenPassword = request.getPassword();

        String storedPassword = getStoredPassword(username);

        boolean verified = HashUtil.validatePassword(givenPassword, storedPassword);

        if (verified) {
            //add login action to auth table
            String authToken = authDAO.login(username);
            return new LoginResponse(true, authToken);
        } else {
            return new LoginResponse(false, "Your username or password is invalid.", null);
        }
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        LogoutResponse response = authDAO.sessionIsValid(request.getAuthToken()) ?
                new LogoutResponse(true) :
                new LogoutResponse(true, "Your session has timed out. You were logged out automatically.");
        authDAO.logout(request.getAuthToken(), request.getUsername());
        return response;
    }

    public User findUser(String username) {
        for (User u : hardCodedUsers) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }
        return null;
    }

    private String getStoredPassword(String username) {
        String result = null;
        try {
            Item item = table.getItem("username", username);

            System.out.println("Printing item after retrieving it....");
            System.out.println(item.toJSONPretty());
            result = (String) item.get("passwordHash");
        } catch (Exception e) {
            System.err.println("GetItem failed.");
            System.err.println(e.getMessage());
        }
        return result;
    }


    public void writeToUserTable(User user, String passwordHash) {
        String username = user.getUsername();
        String handle = user.getAlias();

        try {
            System.out.println("Adding a new user...");
            PutItemOutcome outcome = table
                    .putItem(
                            new Item()
                                    .withPrimaryKey("username", username)
                                    .withPrimaryKey("handle", handle)
                                    .withString("passwordHash", passwordHash)
                                    .withString("name", user.getName())
                                    .withString("imageURL", user.getImageUrl())
                    );

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());
        } catch (Exception e) {
            System.err.println("Unable to add user: " + username);
            System.err.println(e.getMessage());
        }
    }

    public boolean userExistsWithUsername(String username) {
        Item item = null;
        try {
            item = table.getItem("username", username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item != null;
    }

    public boolean userExistsWithHandle(String handle) {
        Index index = table.getIndex("handle-index");

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("#h = :v_handle")
                .withNameMap(new NameMap()
                        .with("#h", "handle"))
                .withValueMap(new ValueMap()
                        .withString(":v_handle", handle));

        ItemCollection<QueryOutcome> items = index.query(spec);
        Iterator<Item> iterator = items.iterator();

        List<Object> results = new ArrayList<>();
        while (iterator.hasNext()) {
            results.add(iterator.next());
        }
        return !results.isEmpty();
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        ScanRequest scanRequest = new ScanRequest().withTableName(table.getTableName());
        ScanResult result = client.scan(scanRequest);
        for (Map<String, AttributeValue> item : result.getItems()) {
            System.out.println(item);
            users.add(new User(){{
                setName(String.valueOf(item.get("name").getS()));
                setAlias(String.valueOf(item.get("handle").getS()));
                setImageUrl(String.valueOf(item.get("imageURL").getS()));
                setUsername(String.valueOf(item.get("username").getS()));
            }});
        }
        return users;
    }
}
