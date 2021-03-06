/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author jlake
 */
public class QuestionOption extends Question implements Serializable {

    private static final long serialVersionUID = 123456789L;

    private String[] options;

    private int[] scores;

    private SimpleStringProperty answerString = new SimpleStringProperty();

    private SimpleIntegerProperty score = new SimpleIntegerProperty();

    public QuestionOption(int idx, String category, String text, String[] options) {
        this(idx, category, text, options, new int[options.length]);
    }

    public QuestionOption(int idx, String category, String text, String[] opts, int[] scoresList) {
        super(idx, category, text);
        this.commentQType = Question.COMMENT_QTYPE_OPT;
        this.options = opts;
        this.scores = scoresList;
        bindProperties();
    }

    private void bindProperties() {
        responseIdx.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
                if (options != null && newVal.intValue() >= 0 && newVal.intValue() < options.length) {
                    answerString.set(options[newVal.intValue()]);
                    score.set(scores[newVal.intValue()]);
                } else {
                    score.set(0);
                    answerString.set("No Answer");
                }
            }
        });
    }

    @Override
    public String getAnswerString() {
        if (this.responseIdx.get() >= 0 && this.responseIdx.get() < options.length) {
            return options[this.responseIdx.get()];
        } else {
            return "No Answer";
        }
    }

    @Override
    public void setAnswer(String ans) {
        for (int opIdx = 0; opIdx < options.length; opIdx++) {
            if (options[opIdx].equalsIgnoreCase(ans)) {
                this.responseIdx.set(opIdx);
                return;
            }
        }
        // If the answer is not found, set to -1 (unanswered)
        this.responseIdx.set(-1);
    }

    @Override
    public boolean isYes() {
        return false;
    }

    @Override
    public boolean isTypeYesNo() {
        return false;
    }

    public String getOption(int optIdx) {
        if (optIdx < 0 || optIdx >= options.length) {
            return "Select Answer";
        } else {
            return options[optIdx];
        }
    }

    public int getOptionScore() {
        if (responseIdx.get() >= 0 && scores != null) {
            return scores[responseIdx.get()];
        }
        return 0;
    }

    public String[] getOptions() {
        return options;
    }

    public SimpleStringProperty answerStringProperty() {
        return answerString;
    }

    public SimpleIntegerProperty scoreProperty() {
        return score;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.writeObject(getAnswerString());
        s.writeObject(getOptions());
        s.writeInt(score.get());
        s.writeObject(scores);
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        answerString = new SimpleStringProperty((String) s.readObject());
        options = (String[]) s.readObject();
        score = new SimpleIntegerProperty(s.readInt());
        scores = (int[]) s.readObject();

        bindProperties();
    }
}
