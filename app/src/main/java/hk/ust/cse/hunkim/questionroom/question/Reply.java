package hk.ust.cse.hunkim.questionroom.question;

import java.util.Date;

/**
 * Created by Jonathan on 11/21/2015.
 */
public class Reply implements Comparable<Reply>{

    private String key;
    private String head;
    private int echo;
    private boolean hidden;
    private boolean highlighted;
    private int order;
    private long timestamp;

    public Reply(String head) {
        this.head = head;

        this.echo = 0;
        this.hidden = false;
        this.highlighted = false;
        this.timestamp = new Date().getTime();
    }

    public Reply(){

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getEcho() {
        return echo;
    }

    @Override
    public int compareTo(Reply another) {
        return 0;
    }
}
