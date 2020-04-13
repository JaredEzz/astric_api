//package astric.server.queue;
//import com.amazonaws.regions.Regions;
//import com.amazonaws.services.sqs.AmazonSQS;
//import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
//import com.amazonaws.services.sqs.model.SendMessageRequest;
//import com.amazonaws.services.sqs.model.SendMessageResult;
//
//
//public class SqsClient {
//
//    public static void main(String[] args) {
//
//
//        String messageBody = "newMessage999";
//
//        String queueUrl = "https://sqs.us-west-2.amazonaws.com/765610589252/astric";
//
//
//        SendMessageRequest send_msg_request = new SendMessageRequest()
//
//                .withQueueUrl(queueUrl)
//
//                .withMessageBody(messageBody)
//
//                .withDelaySeconds(5);
//
//
//        AmazonSQS sqs = AmazonSQSClientBuilder.standard()
//                .withRegion(Regions.US_WEST_2)
//                .build();
//
//        SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);
//
//
//        String msgId = send_msg_result.getMessageId();
//
//        System.out.println("Message ID: " + msgId);
//
//    }
//
//}