package fi.helsinki.cs.tmc.core.domain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FeedbackQuestionTest {

    private FeedbackQuestion question;

    @Before
    public void setUp() {

        question = new FeedbackQuestion();
        question.setId(1);
        question.setKind("kind1");
        question.setQuestion("question1");
    }

    @Test
    public void getters() {

        assertEquals(question.getId(), 1);
        assertEquals(question.getKind(), "kind1");
        assertEquals(question.getQuestion(), "question1");
    }

    @Test
    public void matchersTest() {

        assertFalse(question.isText());
        assertFalse(question.isIntRange());
        question.setKind("text");
        assertTrue(question.isText());
        question.setKind("intrange[-2312..234]");
        assertTrue(question.isIntRange());
    }

    @Test(expected = IllegalStateException.class)
    public void getIntRangeMaxWhenKindIsNotIntRangeTest() {

        question.getIntRangeMax();
    }

    @Test(expected = IllegalStateException.class)
    public void getIntRangeMinWhenKindIsNotIntRangeTest() {

        question.getIntRangeMin();
    }

    @Test
    public void intRangesTest() {

        question.setKind("intrange[-2312..234]");
        assertEquals(question.getIntRangeMin(), -2312);
        assertEquals(question.getIntRangeMax(), 234);
    }

}
