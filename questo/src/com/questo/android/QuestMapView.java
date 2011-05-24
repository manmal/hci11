package com.questo.android;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.maps.MapActivity;
import com.questo.android.common.Constants;
import com.questo.android.model.Place;
import com.questo.android.model.Tournament;
import com.questo.android.view.QuestoMapView;
import com.questo.android.view.TopBar;

public class QuestMapView extends MapActivity {

	public static final String EXTRA_TOURNAMENT_UUID = "EXTRA_TOURNAMENT_UUID";
	
	public final static int ADD_PLACE_REQUEST_CODE = 1;
	public final static int ADD_QUESTION_REQUEST_CODE = 2;

	private QuestoMapView questMap;
	private Place currentPlace;
	private Tournament tournament; //conditional, if is null, no tournament has been set -> display all places

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initTournamentExtra();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private void initTournamentExtra() {
		if (getIntent().getExtras() == null)
			return;
		String tournamentUUID = getIntent().getExtras().getString(EXTRA_TOURNAMENT_UUID);
		if(tournamentUUID != null) {
			tournament = ((App)getApplicationContext()).getModelManager().getGenericObjectByUuid(tournamentUUID, Tournament.class);
			if (tournament != null) {
				questMap.restrictToPlacesFromTournament(tournament);
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View tournamentBanner = inflater.inflate(R.layout.quest_map_tournament_banner, null);
				LinearLayout mapLayout = (LinearLayout)findViewById(R.id.QuestMapLayout);
				TextView bannerText = (TextView)tournamentBanner.findViewById(R.id.tournament_banner_text);
				bannerText.setText(Html.fromHtml("Showing only tournament <b>" + tournament.getName() + "</b>"));
				Button showAllBtn = (Button)tournamentBanner.findViewById(R.id.show_all);
				showAllBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						removeTournamentRestriction();
					}
				});
				mapLayout.addView(tournamentBanner, 0);
			}
		}
	}
	
	private void removeTournamentRestriction() {
		RelativeLayout tournamentBanner = (RelativeLayout)findViewById(R.id.tournament_banner);
		tournamentBanner.setVisibility(View.GONE);
		tournament = null;
		questMap.showAllPlaces();
	}
	
	private void initView() {
		setContentView(R.layout.quest_map);
		TopBar topBar = (TopBar) findViewById(R.id.topbar);
		Button addQuestionBtn = topBar.addImageButtonLeftMost(
				this.getApplicationContext(), R.drawable.img_plus);
		Button showListBtn = topBar.addImageToggleButtonLeftMost(
				this.getApplicationContext(), R.drawable.img_list, false);
		showListBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent showListView = new Intent(QuestMapView.this,
						QuestListView.class);
				startActivity(showListView);
			}
		});
		addQuestionBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				QuestMapView.this.openContextMenu(v);
			}
		});
		registerForContextMenu(addQuestionBtn);
		questMap = (QuestoMapView) findViewById(R.id.QuestMap);
		questMap.setShowDetails(true);
	}

	@Override
	public void onBackPressed() {
		if (tournament != null) {
			Intent intent = new Intent(this, TournamentDetailsView.class);
			intent.putExtra(Constants.TRANSITION_OBJECT_UUID, tournament.getUuid());
			intent.putExtra(TournamentDetailsView.EXTRA_STARTED_BY, TournamentDetailsView.ReturnTo.RETURN_TO_TOURNAMENTS_OVERVIEW.ordinal());
			startActivity(intent);
		}  
		else
			startActivity(new Intent(this, HomeView.class));
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		MenuItem addPlace = menu.add("Add Place");
		MenuItem addQuestion = menu.add("Add Question");
		addPlace.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent addPlaceIntent = new Intent(QuestMapView.this,
						AddPlace.class);
				startActivityForResult(addPlaceIntent, ADD_PLACE_REQUEST_CODE);
				return true;
			}
		});
		addQuestion.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (QuestMapView.this.currentPlace != null) {
					List<String> places  = QuestMapView.this.questMap.getSelectedPlacesUuid();
					String[] array = new String[0];
					Intent addQuestionIntent = new Intent(QuestMapView.this,
							AddQuestion.class);
					addQuestionIntent.putExtra(
							Constants.TRANSITION_OBJECT_UUID, places.toArray(array));
					startActivityForResult(addQuestionIntent,
							ADD_QUESTION_REQUEST_CODE);
					return true;
				}
				return false;
			}
		});
	}
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(questMap!=null){
			questMap.refresh();
		}
	}
}
