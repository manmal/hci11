package com.questo.android.model.test;

import android.text.Html;

import com.questo.android.ModelManager;
import com.questo.android.helper.UUIDgen;
import com.questo.android.model.Place;
import com.questo.android.model.PossibleAnswer;
import com.questo.android.model.PossibleAnswerMultipleChoice;
import com.questo.android.model.Question;
import com.questo.android.model.Question.Type;

public class TestData {

	public static void generateTestData(ModelManager manager) {
		Place stephansdom = new Place(UUIDgen.getUUID(), "Stephansdom");
		manager.create(stephansdom, Place.class);
		Place peterskirche = new Place(UUIDgen.getUUID(), "Peterskirche");
		manager.create(peterskirche, Place.class);
		
		Question q_stephansdom_1 = new Question(UUIDgen.getUUID(), Type.MULTIPLE_CHOICE, "How high is the southern tower of the Stephansdom?");
		PossibleAnswer q_stephansdom_1_answer_1 = new PossibleAnswerMultipleChoice(1, "136,4 meters", true);
		PossibleAnswer q_stephansdom_1_answer_2 = new PossibleAnswerMultipleChoice(2, "133,2 meters", false);
		PossibleAnswer q_stephansdom_1_answer_3 = new PossibleAnswerMultipleChoice(3, "145,9 meters", false);
		PossibleAnswer[] q_stephansdom_1_answers = new PossibleAnswer[]{q_stephansdom_1_answer_1, q_stephansdom_1_answer_2, q_stephansdom_1_answer_3};
		q_stephansdom_1.getPossibleAnswers().setAll(q_stephansdom_1_answers);
		q_stephansdom_1.getCorrectAnswer().set(q_stephansdom_1_answer_1);
		manager.create(q_stephansdom_1, Question.class);
		q_stephansdom_1.setPlace(stephansdom);
		manager.update(q_stephansdom_1, Question.class);
		
		
	}
	
}