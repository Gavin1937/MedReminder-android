package java.cs3337.medreminder_android;

public class NotificationMessage {

    NotificationMessage(String title, String message)
    {
        this.title = title;
        this.message = message;
    }

    public void setVisited()
    {
        this.visited = true;
    }

    public boolean isVisited()
    {
        return this.visited;
    }
    public String getTitle()
    {
        return this.title;
    }
    public String getMessage()
    {
        return this.message;
    }


    private boolean visited = false;
    private String title;
    private String message;
}
