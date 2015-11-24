package hk.ust.cse.hunkim.questionroom;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.TestCase;

import hk.ust.cse.hunkim.questionroom.question.Reply;


/**
 * Created by hunkim on 7/15/15.
 */

public class ReplyTest extends TestCase {
    Reply reply;

    protected void setUp() throws Exception {
        super.setUp();

        reply = new Reply("Hello? This is very nice");
    }

    @SmallTest
    public void testHead() {
        assertEquals("Head?", "Hello?", reply.getHead());
    }

    @SmallTest
    public void testOrder(){
        assertEquals("Order", 0, reply.getOrder());
    }

    @SmallTest
    public void testNecho(){
        assertEquals("Necho", 0, reply.getEcho());
    }

    @SmallTest
    public void testHighlighted(){
        assertEquals("Highlighted", false, reply.isHighlighted());
    }

    @SmallTest
    public void testHidden(){
        assertEquals("Hidden", false, reply.isHidden());
    }


}
