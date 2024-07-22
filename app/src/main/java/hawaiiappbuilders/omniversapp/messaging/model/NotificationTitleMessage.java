package hawaiiappbuilders.omniversapp.messaging.model;

/**
 * Model used to hold notification contentTitle and contentText
 */
public class NotificationTitleMessage {
   public String title;
   public String body;

   public NotificationTitleMessage() {

   }

   public NotificationTitleMessage(String title, String body) {
      this.title = title;
      this.body = body;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getBody() {
      return body;
   }

   public void setBody(String body) {
      this.body = body;
   }
}
