package com.hokaze.pathfinderbackgroundgenerator;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private AdView mAdView;
    private FirebaseAnalytics mFirebaseAnalytics;

    private Button bGenerate;
    private TextView tvResults;
    private Spinner spinRace, spinClass, spinAlignment;

    private String playerRace, playerClass, allowedAlignments, apparentSex;
    private int d100, d20, arrayIndex, bioSiblings, adoptSiblings, halfSiblings, conflictPoints;
    int classAge, characterAge, heightFt, heightIn, weightLb;
    private Random ran;
    private Boolean lowerClass, noble, adopted, adoptedOutsideRace, criminal, forLove, alignNonLawful, alignNeutral, alignLawful, alignGood, noValidAlignments;
    private Boolean finalAlignments[] = new Boolean[9];

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu - this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp:
                // User chose the "Help" item, show the default text explaining rules and app purpose
                showHelpDialog();
                return true;

            case R.id.menuLegal:
                // User chose the "Legal" action, display Open Game License
                showLegalDialog();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Load ad banner
        mAdView = (AdView)findViewById(R.id.adView);
        // Deploy test ads to emulator and test physical device, other devices will get real ads
        // (Don't want to accidentally click my own ads and get suspended!)
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR) // All emulators
                .addTestDevice("9E352341EE24936E89C7621696793500")  // My Phone as test device
                .build();
        mAdView.loadAd(adRequest);

        // Allow us to access our widgets from the code
        bGenerate = (Button)findViewById(R.id.generateButton);
        tvResults = (TextView)findViewById(R.id.bgTextView);
        tvResults.setMovementMethod(new ScrollingMovementMethod()); // scrollable text
        spinRace = (Spinner)findViewById(R.id.raceSpinner);
        spinClass = (Spinner)findViewById(R.id.classSpinner);
        spinAlignment = (Spinner)findViewById(R.id.alignmentSpinner);

        // When button is hit we need to get info from spinners then run through the random tables
        bGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bGenerate.setEnabled(false); // disable button until task is finished so repeatedly clicking won't cause problems
                tvResults.scrollTo(0,0); // reset scrollbar position

                // Chosen race, class and allowed alignments from dropdown
                playerRace = spinRace.getSelectedItem().toString();
                playerClass = spinClass.getSelectedItem().toString();
                allowedAlignments = spinAlignment.getSelectedItem().toString();

                // In addition to campaign alignment restrictions, some classes must be specific alignments
                alignNonLawful = false;
                alignNeutral = false;
                alignLawful = false;
                alignGood = false;

                if ("Barbarian".equals(playerClass)) {
                    alignNonLawful = true;
                }
                else if ("Druid".equals(playerClass)) {
                    alignNeutral = true;
                }
                else if ("Monk".equals(playerClass)) {
                    alignLawful = true;
                }
                else if ("Paladin".equals(playerClass)) {
                    alignLawful = true;
                    alignGood = true;
                }

                // BEGIN THE RANDOM TABLES
                ran = new Random();
                tvResults.setText("");


                //-----------------------------//
                // STEP 0: Physical Appearance //
                //-----------------------------//
                // Generates data from ARG tables
                physicalSex();
                classAgeCategory();
                racialAgeWeightHeight();
                // Formats and prints
                physicalAttributes();
                // Eye/Hair colour, adjusted by race
                eyesAndHair();

                //----------------------------------------//
                // Step 1: Homeland, Family and Childhood //
                //----------------------------------------//
                tvResults.append(getResources().getTextArray(R.array.sectionHeaders)[0]);
                tvResults.append("\n\n");

                // Dwarven Homeland and Family
                if ("Dwarf".equals(playerRace)) {
                    // Print background info about race
                    tvResults.append(getResources().getTextArray(R.array.racialInfo)[0]);
                    tvResults.append("\n\n\n");
                    // Racial tables
                    dwarfHomeTable();
                    dwarfParentTable();
                    dwarfSiblingTable();
                }
                // Elven Homeland and Family
                else if ("Elf".equals(playerRace)) {
                    // Print background info about race
                    tvResults.append(getResources().getTextArray(R.array.racialInfo)[1]);
                    tvResults.append("\n\n\n");
                    // Racial tables
                    elfHomeTable();
                    elfParentTable();
                    elfSiblingTable();
                }
                // Gnomish Homeland and Family
                else if ("Gnome".equals(playerRace)) {
                    // Print background info about race
                    tvResults.append(getResources().getTextArray(R.array.racialInfo)[2]);
                    tvResults.append("\n\n\n");
                    // Racial tables
                    gnomeHomeTable();
                    gnomeParentTable();
                    gnomeSiblingTable();
                }
                // Etc
                else if ("Half-Elf".equals(playerRace)) {
                    // Print background info about race
                    tvResults.append(getResources().getTextArray(R.array.racialInfo)[3]);
                    tvResults.append("\n\n\n");
                    // Racial tables
                    halfElfHomeTable();
                    halfElfParentTable();
                    halfElfSiblingTable();
                }
                else if ("Half-Orc".equals(playerRace)) {
                    // Print background info about race
                    tvResults.append(getResources().getTextArray(R.array.racialInfo)[4]);
                    tvResults.append("\n\n\n");
                    // Racial tables
                    halfOrcHomeTable();
                    halfOrcParentTable();
                    halfOrcSiblingTable();

                }
                else if ("Halfling".equals(playerRace)) {
                    // Print background info about race
                    tvResults.append(getResources().getTextArray(R.array.racialInfo)[5]);
                    tvResults.append("\n\n\n");
                    // Racial tables
                    halflingHomeTable();
                    halflingParentTable();
                    halflingSiblingTable();
                }
                else if ("Human".equals(playerRace)) {
                    // Print background info about race
                    tvResults.append(getResources().getTextArray(R.array.racialInfo)[6]);
                    tvResults.append("\n\n\n");
                    // Racial tables
                    humanHomeTable();
                    humanParentTable();
                    humanSiblingTable();
                }

                // Circumstance of Birth
                birthCircumstanceTable();

                // Nobility - adjusted to use Noble Rank OR Parent Profession, NOT both
                if (noble) {
                    nobilityTable();
                }
                // Professions/adoption don't occur for nobles
                else {
                    // Parent's Profession
                    // Title and some text here to make rolling twice for adopted characters easier
                    tvResults.append(getResources().getTextArray(R.array.parentsProfession)[0]);
                    tvResults.append("\n");
                    tvResults.append("Your birth parents are: ");
                    parentsProfessionTable();
                    if (adopted) {
                        tvResults.append("Your adopted parents are: ");
                        parentsProfessionTable();
                    }

                    // Adopted Outside Your Race
                    if (adoptedOutsideRace) {
                        adoptiveParentsTable();
                    }
                }

                // Major Childhood Event
                majorChildhoodEventTable();

                // Crime and Punishment
                if (criminal) {
                    crimeAndPunishmentTable();
                }


                //----------------------------------//
                // Step 2: Adolescence and Training //
                //----------------------------------//
                tvResults.append("\n");
                tvResults.append(getResources().getTextArray(R.array.sectionHeaders)[1]);
                tvResults.append("\n\n");

                // Roll table(s) for player class training
                classTrainingTable();

                // Influential Associates
                associatesTable();


                //------------------------------------------------------//
                // Step 3: Moral Conflicts, Relationships and Drawbacks //
                //------------------------------------------------------//
                tvResults.append("\n");
                tvResults.append(getResources().getTextArray(R.array.sectionHeaders)[2]);
                tvResults.append("\n\n");

                // Roll and resolve conflict, determine alignment
                conflictsTable();
                conflictSubjectTable();
                conflictMotivationTable();
                conflictResolutionTable();
                alignmentGenerator();

                deityAndReligion();
                romanceTable();
                adventurerBondsTable();
                drawbackTable();

                // Re-enable button now task is finished
                bGenerate.setEnabled(true);
            }
        });
    }

    //-----------------------------------------------------//
    // Helper functions for generator's random table rolls //
    //-----------------------------------------------------//

    // Homeland, Family and Childhood Tables

    private void dwarfHomeTable() {
        // Note: random.nextInt rolls between 0 and n-1, so nextInt(100) produces range 00-99. For d100 we need 01-100.
        d100 = ran.nextInt(100)+1;

        if (d100 < 41) {
            arrayIndex = 1;
        }
        else if (d100 < 81) {
            arrayIndex = 2;
        }
        else if (d100 < 88) {
            arrayIndex = 3;
        }
        else if (d100 < 95) {
            arrayIndex = 4;
        }
        else {
            arrayIndex = 5;
        }

        tvResults.append(getResources().getTextArray(R.array.dwarfHomeland)[0]); // "Dwarven Homeland" underlined
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.dwarfHomeland)[arrayIndex]); // Text with formatting for traits
        tvResults.append("\n\n\n");

        if (arrayIndex == 5) {
            unusualHomeTable();
        }
    }
    private void dwarfParentTable() {
        d100 = ran.nextInt(100)+1;

        if (d100 < 61) {
            arrayIndex = 1;
        }
        else if (d100 < 74) {
            arrayIndex = 2;
        }
        else if (d100 < 87) {
            arrayIndex = 3;
        }
        else {
            arrayIndex = 4;
        }

        tvResults.append(getResources().getTextArray(R.array.dwarfParents)[0]);
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.dwarfParents)[arrayIndex]);
        tvResults.append("\n\n\n");
    }
    private void dwarfSiblingTable() {
        d100 = ran.nextInt(100)+1;
        bioSiblings = 0;
        adoptSiblings = 0;

        // Title, e.g "Dwarven Siblings"
        tvResults.append(getResources().getTextArray(R.array.dwarfSiblings)[0]);
        tvResults.append("\n");

        if (d100 < 81) {
            bioSiblings = ran.nextInt(4)+1; // roll 1d4 siblings
            tvResults.append("You have " + bioSiblings + " ");
            tvResults.append(getResources().getTextArray(R.array.dwarfSiblings)[1]);
            if (bioSiblings > 1) {
                tvResults.append(getResources().getTextArray(R.array.dwarfSiblings)[2]);
            }
        }
        else if (d100 < 91) {
            bioSiblings = ran.nextInt(4)+2; // roll 1d4+1 siblings
            tvResults.append("You have " + bioSiblings + " ");
            tvResults.append(getResources().getTextArray(R.array.dwarfSiblings)[3]);
        }
        else if (d100 < 96) {
            bioSiblings = ran.nextInt(3); // roll 1d3-1 siblings
            adoptSiblings = ran.nextInt(3); // roll 1d3-1 adopted siblings
            tvResults.append("You have " + bioSiblings + " ");
            tvResults.append(getResources().getTextArray(R.array.dwarfSiblings)[4]);
            tvResults.append(" " + adoptSiblings + " ");
            tvResults.append(getResources().getTextArray(R.array.dwarfSiblings)[5]);
            if (bioSiblings + adoptSiblings > 1) {
                tvResults.append(getResources().getTextArray(R.array.dwarfSiblings)[6]);
            }
        }
        else {
            // No siblings
            tvResults.append(getResources().getTextArray(R.array.dwarfSiblings)[7]);
        }

        // Display sibling relative ages to you and any adopted races
        tvResults.append("\n");
        if (adoptSiblings + bioSiblings > 0) {
            siblingAgeTable(adoptSiblings + bioSiblings);
        }
        if (adoptSiblings > 0) {
            siblingRaceTable(adoptSiblings);
        }
        tvResults.append("\n");
    }

    private void elfHomeTable() {
        d100 = ran.nextInt(100)+1;
        boolean unusual = false;

        tvResults.append(getResources().getTextArray(R.array.elfHomeland)[0]);
        tvResults.append("\n");

        if (d100 < 61) {
            tvResults.append(getResources().getTextArray(R.array.elfHomeland)[1]);
        }
        else if (d100 < 81) {
            tvResults.append(getResources().getTextArray(R.array.elfHomeland)[2]);
            if (playerRace.equals("Elf")) {
                tvResults.append(getResources().getTextArray(R.array.elfHomeland)[3]);
            }
            else if (playerRace.equals("Half-Elf")) {
                tvResults.append(getResources().getTextArray(R.array.elfHomeland)[4]);
            }
        }
        else if (d100 < 96) {
            tvResults.append(getResources().getTextArray(R.array.elfHomeland)[5]);
            if (playerRace.equals("Elf")) {
                tvResults.append(getResources().getTextArray(R.array.elfHomeland)[6]);
            }
            else if (playerRace.equals("Half-Elf")) {
                tvResults.append(getResources().getTextArray(R.array.elfHomeland)[7]);
            }
        }
        else {
            tvResults.append(getResources().getTextArray(R.array.elfHomeland)[8]);
            if (playerRace.equals("Elf")) {
                tvResults.append(getResources().getTextArray(R.array.elfHomeland)[9]);
            }
            else if (playerRace.equals("Half-Elf")) {
                tvResults.append(getResources().getTextArray(R.array.elfHomeland)[10]);
            }
            unusual = true;
        }


        tvResults.append("\n\n\n");

        if (unusual) {
            unusualHomeTable();
        }
    }
    private void elfParentTable() {
        d100 = ran.nextInt(100)+1;

        if (d100 < 80) {
            arrayIndex = 1;
        }
        else if (d100 < 88) {
            arrayIndex = 2;
        }
        else if (d100 < 96) {
            arrayIndex = 3;
        }
        else {
            arrayIndex = 4;
        }

        tvResults.append(getResources().getTextArray(R.array.elfParents)[0]);
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.elfParents)[arrayIndex]);
        tvResults.append("\n\n\n");
    }
    private void elfSiblingTable() {
        d100 = ran.nextInt(100)+1;
        bioSiblings = 0;
        adoptSiblings = 0;
        halfSiblings = 0;

        tvResults.append(getResources().getTextArray(R.array.elfSiblings)[0]);
        tvResults.append("\n");

        if (d100 < 81) {
            bioSiblings = ran.nextInt(2)+1; // roll 1d2 siblings
            tvResults.append("You have " + bioSiblings + " ");
            tvResults.append(getResources().getTextArray(R.array.elfSiblings)[1]);
            if (bioSiblings > 1) {
                tvResults.append(getResources().getTextArray(R.array.elfSiblings)[2]);
            }
        }
        else if (d100 < 91) {
            bioSiblings = ran.nextInt(4)+2; // roll 1d4+1 siblings
            tvResults.append("You have " + bioSiblings + " ");
            tvResults.append(getResources().getTextArray(R.array.elfSiblings)[3]);
        }
        else if (d100 < 96) {
            bioSiblings = ran.nextInt(4)+2; // roll 1d4+1 siblings
            halfSiblings = ran.nextInt(3); // roll 1d3-1 of those as half-elves
            adoptSiblings = ran.nextInt(3); // roll 1d3-1 adopted siblings
            tvResults.append("You have " + bioSiblings + " ");
            tvResults.append(getResources().getTextArray(R.array.elfSiblings)[4]);
            tvResults.append(" " + halfSiblings + " ");
            tvResults.append(getResources().getTextArray(R.array.elfSiblings)[5]);
            tvResults.append(" " + adoptSiblings + " ");
            tvResults.append(getResources().getTextArray(R.array.elfSiblings)[6]);
        }
        else {
            // No siblings
            tvResults.append(getResources().getTextArray(R.array.elfSiblings)[7]);
        }

        // Display sibling relative ages to you and any adopted races
        tvResults.append("\n");
        if (adoptSiblings + bioSiblings > 0) {
            siblingAgeTable(adoptSiblings + bioSiblings);
        }
        if (adoptSiblings > 0) {
            siblingRaceTable(adoptSiblings);
        }
        tvResults.append("\n");
    }

    private void gnomeHomeTable() {
        d100 = ran.nextInt(100)+1;

        if (d100 < 31) {
            arrayIndex = 1;
        }
        else if (d100 < 66) {
            arrayIndex = 2;
        }
        else if (d100 < 96) {
            arrayIndex = 3;
        }
        else {
            arrayIndex = 4;
        }

        tvResults.append(getResources().getTextArray(R.array.gnomeHomeland)[0]);
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.gnomeHomeland)[arrayIndex]);
        tvResults.append("\n\n\n");

        if (arrayIndex == 4) {
            unusualHomeTable();
        }
    }
    private void gnomeParentTable() {
        d100 = ran.nextInt(100)+1;

        if (d100 < 91) {
            arrayIndex = 1;
        }
        else if (d100 < 94) {
            arrayIndex = 2;
        }
        else if (d100 < 97) {
            arrayIndex = 3;
        }
        else {
            arrayIndex = 4;
        }

        tvResults.append(getResources().getTextArray(R.array.gnomeParents)[0]);
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.gnomeParents)[arrayIndex]);
        tvResults.append("\n\n\n");
    }
    private void gnomeSiblingTable() {
        d100 = ran.nextInt(100)+1;
        bioSiblings = 0;
        adoptSiblings = 0;

        tvResults.append(getResources().getTextArray(R.array.gnomeSiblings)[0]);
        tvResults.append("\n");

        if (d100 < 51) {
            bioSiblings = ran.nextInt(4)+1; // roll 1d4 siblings
            tvResults.append("You have " + bioSiblings + " ");
            tvResults.append(getResources().getTextArray(R.array.gnomeSiblings)[1]);
            if (bioSiblings > 1) {
                tvResults.append(getResources().getTextArray(R.array.gnomeSiblings)[2]);
            }
        }
        else if (d100 < 61) {
            bioSiblings = ran.nextInt(4); // roll 1d4-1 siblings
            adoptSiblings = 1; // exactly one adopted sibling
            tvResults.append("You have " + bioSiblings + " ");
            tvResults.append(getResources().getTextArray(R.array.gnomeSiblings)[3]);
        }
        else {
            // No siblings
            tvResults.append(getResources().getTextArray(R.array.gnomeSiblings)[4]);
        }

        // Display sibling relative ages to you and any adopted races
        tvResults.append("\n");
        if (adoptSiblings + bioSiblings > 0) {
            siblingAgeTable(adoptSiblings + bioSiblings);
        }
        if (adoptSiblings > 0) {
            siblingRaceTable(adoptSiblings);
        }
        tvResults.append("\n");
    }

    private void halfElfHomeTable() {
        d100 = ran.nextInt(100)+1;

        if (d100 < 26) {
            arrayIndex = 1;
        }
        else if (d100 < 76) {
            arrayIndex = 2;
        }
        else if (d100 < 96) {
            arrayIndex = 3;
        }
        else {
            arrayIndex = 4;
        }

        tvResults.append(getResources().getTextArray(R.array.halfElfHomeland)[0]);
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.halfElfHomeland)[arrayIndex]);
        tvResults.append("\n\n\n");

        // Elven, Human or Unusual Homeland, roll again for location
        if (arrayIndex == 1) {
            elfHomeTable();
        }
        else if (arrayIndex == 2) {
            humanHomeTable();
        }
        else if (arrayIndex == 4) {
            unusualHomeTable();
        }
    }
    private void halfElfParentTable() {
        d100 = ran.nextInt(100)+1;

        if (d100 < 21) {
            arrayIndex = 1;
        }
        else if (d100 < 56) {
            arrayIndex = 2;
        }
        else if (d100 < 91) {
            arrayIndex = 3;
        }
        else {
            arrayIndex = 4;
        }

        tvResults.append(getResources().getTextArray(R.array.halfElfParents)[0]);
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.halfElfParents)[arrayIndex]);
        tvResults.append("\n\n\n");
    }
    private void halfElfSiblingTable() {
        d100 = ran.nextInt(100)+1;
        bioSiblings = 0;

        tvResults.append(getResources().getTextArray(R.array.halfElfSiblings)[0]);
        tvResults.append("\n");

        if (d100 < 21) {
            bioSiblings = ran.nextInt(2); // roll 1d2 siblings
            tvResults.append("You have " + bioSiblings + " ");
            tvResults.append(getResources().getTextArray(R.array.halfElfSiblings)[1]);
            if (bioSiblings > 1) {
                tvResults.append(getResources().getTextArray(R.array.halfElfSiblings)[2]);
            }
        }
        else if (d100 < 31) {
            bioSiblings = 1; // 1 half-elf sibling
            tvResults.append("You have " + bioSiblings + " ");
            tvResults.append(getResources().getTextArray(R.array.halfElfSiblings)[3]);
        }
        else {
            // No siblings
            tvResults.append(getResources().getTextArray(R.array.halfElfSiblings)[4]);
        }

        // Display sibling relative ages to you
        tvResults.append("\n");
        if (bioSiblings > 0) {
            siblingAgeTable(bioSiblings);
        }
        tvResults.append("\n");
    }

    private void halfOrcHomeTable() {
        d100 = ran.nextInt(100)+1;
        if (d100 < 26) {
            arrayIndex = 1;
        }
        else if (d100 < 61) {
            arrayIndex = 2;
        }
        else if (d100 < 76) {
            arrayIndex = 3;
        }
        else if (d100 < 91) {
            arrayIndex = 4;
        }
        else {
            arrayIndex = 5;
        }
        tvResults.append(getResources().getTextArray(R.array.halfOrcHomeland)[0]);
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.halfOrcHomeland)[arrayIndex]);
        tvResults.append("\n\n\n");

        // Human or Unusual Homeland, roll again for location
        if (arrayIndex == 3) {
            humanHomeTable();
        }
        else if (arrayIndex == 5) {
            unusualHomeTable();
        }
    }
    private void halfOrcParentTable() {
        d100 = ran.nextInt(100)+1;

        if (d100 < 11) {
            arrayIndex = 1;
        }
        else if (d100 < 36) {
            arrayIndex = 2;
        }
        else if (d100 < 61) {
            arrayIndex = 3;
        }
        else {
            arrayIndex = 4;
        }

        tvResults.append(getResources().getTextArray(R.array.halfOrcParents)[0]);
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.halfOrcParents)[arrayIndex]);
        tvResults.append("\n\n\n");
    }
    private void halfOrcSiblingTable() {
        d100 = ran.nextInt(100)+1;
        bioSiblings = 0;

        tvResults.append(getResources().getTextArray(R.array.halfOrcSiblings)[0]);
        tvResults.append("\n");

        if (d100 < 61) {
            bioSiblings = ran.nextInt(6)+2; // roll 1d6+1 Orc siblings
            tvResults.append("You have " + bioSiblings + " ");
            tvResults.append(getResources().getTextArray(R.array.halfOrcSiblings)[1]);
        }
        else if (d100 < 71) {
            bioSiblings = ran.nextInt(4)+2; // roll 1d4+1 siblings
            tvResults.append("You have " + bioSiblings + " ");
            tvResults.append(getResources().getTextArray(R.array.halfOrcSiblings)[2]);
            if (bioSiblings > 1) {
                tvResults.append(getResources().getTextArray(R.array.halfOrcSiblings)[3]);
            }
        }
        else if (d100 < 81) {
            bioSiblings = 1; // one fellow half-orc
            tvResults.append("You have " + bioSiblings + " ");
            tvResults.append(getResources().getTextArray(R.array.halfOrcSiblings)[4]);
        }
        else {
            // No siblings
            tvResults.append(getResources().getTextArray(R.array.halfOrcSiblings)[5]);
        }

        // Display sibling relative ages to you
        tvResults.append("\n");
        if (bioSiblings > 0) {
            siblingAgeTable(bioSiblings);
        }
        tvResults.append("\n");
    }

    private void halflingHomeTable() {
        d100 = ran.nextInt(100)+1;

        if (d100 < 51) {
            arrayIndex = 1;
        }
        else if (d100 < 81) {
            arrayIndex = 2;
        }
        else if (d100 < 96) {
            arrayIndex = 3;
        }
        else {
            arrayIndex = 4;
        }

        tvResults.append(getResources().getTextArray(R.array.halflingHomeland)[0]);
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.halflingHomeland)[arrayIndex]);
        tvResults.append("\n\n\n");

        if (arrayIndex == 4) {
            unusualHomeTable();
        }
    }
    private void halflingParentTable() {
        d100 = ran.nextInt(100)+1;

        if (d100 < 71) {
            arrayIndex = 1;
        }
        else if (d100 < 81) {
            arrayIndex = 2;
        }
        else if (d100 < 91) {
            arrayIndex = 3;
        }
        else {
            arrayIndex = 4;
        }

        tvResults.append(getResources().getTextArray(R.array.halflingParents)[0]);
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.halflingParents)[arrayIndex]);
        tvResults.append("\n\n\n");
    }
    private void halflingSiblingTable() {
        d100 = ran.nextInt(100)+1;
        bioSiblings = 0;

        tvResults.append(getResources().getTextArray(R.array.halflingSiblings)[0]);
        tvResults.append("\n");

        if (d100 < 31) {
            bioSiblings = ran.nextInt(2)+1; // roll 1d2 siblings
            tvResults.append("You have " + bioSiblings + " ");
            tvResults.append(getResources().getTextArray(R.array.halflingSiblings)[1]);
            if (bioSiblings > 1) {
                tvResults.append(getResources().getTextArray(R.array.halflingSiblings)[2]);
            }
        }
        else if (d100 < 91) {
            bioSiblings = ran.nextInt(4)+2; // roll 1d4+1 siblings
            tvResults.append("You have " + bioSiblings + " ");
            tvResults.append(getResources().getTextArray(R.array.halflingSiblings)[3]);
        }
        else {
            // No siblings
            tvResults.append(getResources().getTextArray(R.array.halflingSiblings)[4]);
        }

        // Display sibling relative ages to you and any adopted races
        tvResults.append("\n");
        if (bioSiblings > 0) {
            siblingAgeTable(bioSiblings);
        }
        tvResults.append("\n");
    }

    private void humanHomeTable() {
        d100 = ran.nextInt(100)+1;
        boolean unusual = false;

        tvResults.append(getResources().getTextArray(R.array.humanHomeland)[0]);
        tvResults.append("\n");

        if (d100 < 51) {
            tvResults.append(getResources().getTextArray(R.array.humanHomeland)[1]);
        }
        else if (d100 < 86) {
            tvResults.append(getResources().getTextArray(R.array.humanHomeland)[2]);
            switch (playerRace) {
                case "Human":
                    tvResults.append(getResources().getTextArray(R.array.humanHomeland)[3]);
                    break;
                case "Half-Elf":
                    tvResults.append(getResources().getTextArray(R.array.humanHomeland)[4]);
                    break;
                case "Half-Orc":
                    tvResults.append(getResources().getTextArray(R.array.humanHomeland)[5]);
                    break;
            }
        }
        else if (d100 < 96) {
            tvResults.append(getResources().getTextArray(R.array.humanHomeland)[6]);
        }
        else {
            tvResults.append(getResources().getTextArray(R.array.humanHomeland)[7]);
            unusual = true;
        }

        tvResults.append("\n\n\n");

        if (unusual) {
            unusualHomeTable();
        }
    }
    private void humanParentTable() {
        d100 = ran.nextInt(100)+1;

        if (d100 < 51) {
            arrayIndex = 1;
        }
        else if (d100 < 74) {
            arrayIndex = 2;
        }
        else if (d100 < 91) {
            arrayIndex = 3;
        }
        else {
            arrayIndex = 4;
        }

        tvResults.append(getResources().getTextArray(R.array.humanParents)[0]);
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.humanParents)[arrayIndex]);
        tvResults.append("\n\n\n");
    }
    private void humanSiblingTable() {
        d100 = ran.nextInt(100)+1;
        bioSiblings = 0;

        tvResults.append(getResources().getTextArray(R.array.humanSiblings)[0]);
        tvResults.append("\n");

        if (d100 < 41) {
            bioSiblings = ran.nextInt(2)+1; // roll 1d2 siblings
            tvResults.append("You have " + bioSiblings + " ");
            tvResults.append(getResources().getTextArray(R.array.humanSiblings)[1]);
            if (bioSiblings > 1) {
                tvResults.append(getResources().getTextArray(R.array.humanSiblings)[2]);
            }
        }
        else if (d100 < 71) {
            bioSiblings = ran.nextInt(2)+1; // roll 1d2 siblings
            halfSiblings = ran.nextInt(2)+1; // roll 1d2 half-orc/half-elf siblings
            tvResults.append("You have " + bioSiblings + " ");
            tvResults.append(getResources().getTextArray(R.array.humanSiblings)[3]);
            tvResults.append(" " + halfSiblings + " ");
            tvResults.append(getResources().getTextArray(R.array.humanSiblings)[4]);
        }
        else if (d100 < 91) {
            bioSiblings = ran.nextInt(4)+1 + ran.nextInt(4)+1; // roll 2d4 siblings
            tvResults.append("You have " + bioSiblings + " ");
            tvResults.append(getResources().getTextArray(R.array.humanSiblings)[5]);
        }
        else {
            // No siblings
            tvResults.append(getResources().getTextArray(R.array.humanSiblings)[6]);
        }

        // Display sibling relative ages to you and any adopted races
        tvResults.append("\n");
        if (bioSiblings + halfSiblings > 0) {
            siblingAgeTable(bioSiblings + halfSiblings);
        }
        if (halfSiblings > 0) {
            for (int i=0; i < halfSiblings; ++i) {
                d100 = ran.nextInt(100)+1;
                if (d100 < 51) {
                    tvResults.append("One of your half-siblings is a Half-Elf.");
                }
                else {
                    tvResults.append("One of your half-siblings is a Half-Orc.");
                }
            }
        }
        tvResults.append("\n");
    }

    private void siblingAgeTable(int siblings) {
        for (int i=0; i < siblings; ++i) {
            d100 = ran.nextInt(100)+1;

            if (d100 < 49) {
                arrayIndex = 0;
            }
            else if (d100 < 97) {
                arrayIndex = 1;
            }
            else {
                arrayIndex = 2;
            }

            tvResults.append(getResources().getTextArray(R.array.siblingAges)[arrayIndex]);
            tvResults.append("\n");
        }
    }
    private void siblingRaceTable(int siblings) {
        for (int i=0; i < siblings; ++i) {
            d100 = ran.nextInt(100)+1;
            boolean useAn = false;

            if (d100 < 2) {
                arrayIndex = 1; // Aasimar
                useAn = true;
            }
            else if (d100 < 3) {
                arrayIndex = 2; // Catfolk
            }
            else if (d100 < 5) {
                arrayIndex = 4; // Changeling
            }
            else if (d100 < 6) {
                arrayIndex = 5; // Dhampir
            }
            else if (d100 < 7) {
                arrayIndex = 6; // Duergar
            }
            else if (d100 < 17) {
                arrayIndex = 7; // Dwarf
            }
            else if (d100 < 27) {
                arrayIndex = 8; // Elf
                useAn = true;
            }
            else if (d100 < 28) {
                arrayIndex = 9; // Fetchling
            }
            else if (d100 < 29) {
                arrayIndex = 10; // Gillman
            }
            else if (d100 < 39) {
                arrayIndex = 11; // Gnome
            }
            else if (d100 < 40) {
                arrayIndex = 12; // Goblin
            }
            else if (d100 < 51) {
                arrayIndex = 13; // Half-Elf
            }
            else if (d100 < 61) {
                arrayIndex = 14; // Half-Orc
            }
            else if (d100 < 71) {
                arrayIndex = 15; // Halfling
            }
            else if (d100 < 72) {
                arrayIndex = 16; // Hobgoblin
            }
            else if (d100 < 82) {
                arrayIndex = 17; // Human
            }
            // Races beyond this point only inhibit single values instead of ranges
            // e.g Ifrit = 82 = arrayIndex18, Kitsune = 83 = arrayIndex19, ..., Wayang = 100 = arrayIndex36
            else {
                arrayIndex = d100 - 65; // BUGFIX: use -65, NOT -64, due to index starting at 0, not 1, idiot!
                // Ifrit, Orc, Oread, Undine all use "an" instead of "a"
                if (d100 == 82 || d100 == 87 || d100 == 88 || d100 == 97) {
                    useAn = true;
                }
            }

            // Display races, using proper grammar where possible
            if (useAn) {
                tvResults.append("One of your adopted siblings is an: ");
            }
            else {
                tvResults.append("One of your adopted siblings is a: ");
            }
            tvResults.append(getResources().getTextArray(R.array.siblingRaces)[arrayIndex]);
            tvResults.append("\n");
        }
    }
    private void unusualHomeTable() {
        d100 = ran.nextInt(100)+1;

        if (d100 < 11) {
            arrayIndex = 1;
        }
        else if (d100 < 26) {
            arrayIndex = 2;
        }
        else if (d100 < 41) {
            arrayIndex = 3;
        }
        else if (d100 < 51) {
            arrayIndex = 4;
        }
        else if (d100 < 61) {
            arrayIndex = 5;
        }
        else if (d100 < 71) {
            arrayIndex = 6;
        }
        else if (d100 < 81) {
            arrayIndex = 7;
        }
        else if (d100 < 86) {
            arrayIndex = 8;
        }
        else if (d100 < 91) {
            arrayIndex = 9;
        }
        else if (d100 < 96) {
            arrayIndex = 10;
        }
        else {
            arrayIndex = 11;
        }

        tvResults.append(getResources().getTextArray(R.array.unusualHomeland)[0]);
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.unusualHomeland)[arrayIndex]);
        tvResults.append("\n\n\n");
    }

    private void birthCircumstanceTable() {
        d100 = ran.nextInt(100)+1;

        // Reset variables generated by this table
        lowerClass = false;
        noble = false;
        adopted = false;
        adoptedOutsideRace = false;
        forLove = false;

        if (d100 < 41) {
            arrayIndex = 1;
            // Lower-Class: roll 2d20 instead of 1d100 on parent profession
            lowerClass = true;
        }
        else if (d100 < 66) {
            arrayIndex = 2;
        }
        else if (d100 < 71) {
            arrayIndex = 3;
            // Noble: roll on Nobility table (instead of parent profession?)
            noble = true;
        }
        else if (d100 < 73) {
            arrayIndex = 4;
            // Adopted Outside Race: roll for parent race
            adoptedOutsideRace = true;
        }
        else if (d100 < 78) {
            arrayIndex = 5;
            // Adopted: roll for parent profession twice
            adopted = true;
        }
        else if (d100 < 82) {
            arrayIndex = 6;
        }
        else if (d100 < 83) {
            arrayIndex = 7;
        }
        else if (d100 < 85) {
            arrayIndex = 8;
        }
        else if (d100 < 86) {
            arrayIndex = 9;
        }
        else if (d100 < 88) {
            arrayIndex = 10;
        }
        else if (d100 < 89) {
            arrayIndex = 11;
        }
        else if (d100 < 91) {
            arrayIndex = 12;
        }
        else if (d100 < 93) {
            arrayIndex = 13;
        }
        else if (d100 < 95) {
            arrayIndex = 14;
        }
        else if (d100 < 96) {
            arrayIndex = 15;
        }
        else if (d100 < 97) {
            arrayIndex = 16;
        }
        else if (d100 < 98) {
            arrayIndex = 17;
        }
        else if (d100 < 99) {
            arrayIndex = 18;
        }
        else if (d100 < 100) {
            arrayIndex = 19;
        }
        else {
            arrayIndex = 20;
        }

        tvResults.append(getResources().getTextArray(R.array.birthCircumstances)[0]);
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.birthCircumstances)[arrayIndex]);
        tvResults.append("\n\n\n");
    }
    private void parentsProfessionTable() {
        // Use 2d20 if lower-class, otherwise use 1d100 as normal
        if (lowerClass) {
            d100 = ran.nextInt(20)+1 + ran.nextInt(20)+1;
        }
        else {
            d100 = ran.nextInt(100)+1;
        }

        if (d100 < 6) {
            arrayIndex = 1;
        }
        else if (d100 < 26) {
            arrayIndex = 2;
        }
        else if (d100 < 31) {
            arrayIndex = 3;
        }
        else if (d100 < 35) {
            arrayIndex = 4;
        }
        else if (d100 < 38) {
            arrayIndex = 5;
        }
        else if (d100 < 41) {
            arrayIndex = 6;
        }
        else if (d100 < 56) {
            arrayIndex = 7;
        }
        else if (d100 < 71) {
            arrayIndex = 8;
        }
        else if (d100 < 86) {
            arrayIndex = 9;
        }
        else if (d100 < 96) {
            arrayIndex = 10;
        }
        else {
            arrayIndex = 11;
        }

        tvResults.append(getResources().getTextArray(R.array.parentsProfession)[arrayIndex]);
        tvResults.append("\n\n\n");
    }
    private void adoptiveParentsTable() {
        d100 = ran.nextInt(100)+1;

        if (d100 < 6) {
            arrayIndex = 1;
        }
        else if (d100 < 11) {
            arrayIndex = 2;
        }
        else if (d100 < 14) {
            arrayIndex = 3;
        }
        else if (d100 < 20) {
            arrayIndex = 4;
        }
        else if (d100 < 26) {
            arrayIndex = 5;
        }
        else if (d100 < 71) {
            arrayIndex = 6;
        }
        else if (d100 < 96) {
            arrayIndex = 7;
        }
        else {
            arrayIndex = 8;
        }

        tvResults.append(getResources().getTextArray(R.array.adoptedParentsRace)[0]);
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.adoptedParentsRace)[arrayIndex]);
        tvResults.append("\n\n\n");
    }
    private void nobilityTable() {
        d100 = ran.nextInt(100)+1;

        if (d100 < 61) {
            arrayIndex = 1;
        }
        else if (d100 < 79) {
            arrayIndex = 2;
        }
        else if (d100 < 86) {
            arrayIndex = 3;
        }
        else if (d100 < 92) {
            arrayIndex = 4;
        }
        else if (d100 < 97) {
            arrayIndex = 5;
        }
        else if (d100 < 100) {
            arrayIndex = 6;
        }
        else {
            arrayIndex = 7;
        }

        tvResults.append(getResources().getTextArray(R.array.nobility)[0]);
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.nobility)[arrayIndex]);
        tvResults.append("\n\n\n");
    }
    private void majorChildhoodEventTable() {
        d100 = ran.nextInt(100)+1;
        criminal = false;

        if (d100 < 6) {
            arrayIndex = 1;
        }
        else if (d100 < 11) {
            arrayIndex = 2;
        }
        else if (d100 < 16) {
            arrayIndex = 3;
        }
        else if (d100 < 21) {
            arrayIndex = 4;
        }
        else if (d100 < 26) {
            arrayIndex = 5;
        }
        else if (d100 < 31) {
            arrayIndex = 6;
        }
        else if (d100 < 36) {
            arrayIndex = 7;
        }
        else if (d100 < 41) {
            arrayIndex = 8;
        }
        else if (d100 < 46) {
            arrayIndex = 9;
        }
        // Troubled First Love: roll a d12 instead of a d20 on the Romantic Relationships table
        else if (d100 < 51) {
            arrayIndex = 10;
            forLove = true;
        }
        // Imprisoned: need to roll on Crime and Punishment table
        else if (d100 < 56) {
            arrayIndex = 11;
            criminal = true;
        }
        else if (d100 < 61) {
            arrayIndex = 12;
        }
        else if (d100 < 66) {
            arrayIndex = 13;
        }
        else if (d100 < 71) {
            arrayIndex = 14;
        }
        else if (d100 < 76) {
            arrayIndex = 15;
        }
        else if (d100 < 81) {
            arrayIndex = 16;
        }
        else if (d100 < 86) {
            arrayIndex = 17;
        }
        else if (d100 < 91) {
            arrayIndex = 18;
        }
        else if (d100 < 96) {
            arrayIndex = 19;
        }
        else {
            arrayIndex = 20;
        }

        tvResults.append(getResources().getTextArray(R.array.majorChildhoodEvent)[0]);
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.majorChildhoodEvent)[arrayIndex]);
        tvResults.append("\n\n\n");
    }
    private void crimeAndPunishmentTable() {
        d100 = ran.nextInt(100)+1;

        if (d100 < 11) {
            arrayIndex = 1;
        }
        else if (d100 < 21) {
            arrayIndex = 2;
        }
        else if (d100 < 31) {
            arrayIndex = 3;
        }
        else if (d100 < 41) {
            arrayIndex = 4;
        }
        else if (d100 < 51) {
            arrayIndex = 5;
        }
        else if (d100 < 61) {
            arrayIndex = 6;
        }
        else if (d100 < 71) {
            arrayIndex = 7;
        }
        else if (d100 < 81) {
            arrayIndex = 8;
        }
        else if (d100 < 91) {
            arrayIndex = 9;
        }
        else {
            arrayIndex = 10;
        }

        tvResults.append(getResources().getTextArray(R.array.crimes)[0]);
        tvResults.append("\n");
        tvResults.append("Your crime: ");
        tvResults.append(getResources().getTextArray(R.array.crimes)[arrayIndex]);

        d100 = ran.nextInt(100)+1;

        if (d100 < 11) {
            arrayIndex = 1;
        }
        else if (d100 < 21) {
            arrayIndex = 2;
        }
        else if (d100 < 31) {
            arrayIndex = 3;
        }
        else if (d100 < 41) {
            arrayIndex = 4;
        }
        else if (d100 < 51) {
            arrayIndex = 5;
        }
        else if (d100 < 61) {
            arrayIndex = 6;
        }
        else if (d100 < 71) {
            arrayIndex = 7;
        }
        else if (d100 < 81) {
            arrayIndex = 8;
        }
        else if (d100 < 91) {
            arrayIndex = 9;
        }
        else {
            arrayIndex = 10;
        }

        tvResults.append("\n");
        tvResults.append("Your punishment: ");
        tvResults.append(getResources().getTextArray(R.array.punishments)[arrayIndex]);
        tvResults.append("\n\n\n");
    }

    // Adolescence and Training Tables
    private void classTrainingTable() {
        d100 = ran.nextInt(100)+1;

        if (d100 < 11) {
            arrayIndex = 1;
        }
        else if (d100 < 21) {
            arrayIndex = 2;
        }
        else if (d100 < 31) {
            arrayIndex = 3;
        }
        else if (d100 < 41) {
            arrayIndex = 4;
        }
        else if (d100 < 51) {
            arrayIndex = 5;
        }
        else if (d100 < 61) {
            arrayIndex = 6;
        }
        else if (d100 < 71) {
            arrayIndex = 7;
        }
        else if (d100 < 81) {
            arrayIndex = 8;
        }
        else if (d100 < 91) {
            arrayIndex = 9;
        }
        else {
            arrayIndex = 10;
        }

        if ("Alchemist".equals(playerClass)) {
            tvResults.append(getResources().getTextArray(R.array.trainingAlchemist)[0]);
            tvResults.append("\n");
            tvResults.append(getResources().getTextArray(R.array.trainingAlchemist)[arrayIndex]);
            tvResults.append("\n\n\n");
        }
        else if ("Barbarian".equals(playerClass)) {
            tvResults.append(getResources().getTextArray(R.array.trainingBarbarian)[0]);
            tvResults.append("\n");
            tvResults.append(getResources().getTextArray(R.array.trainingBarbarian)[arrayIndex]);
            tvResults.append("\n\n\n");
        }
        else if ("Bard".equals(playerClass)) {
            tvResults.append(getResources().getTextArray(R.array.trainingBard)[0]);
            tvResults.append("\n");
            tvResults.append(getResources().getTextArray(R.array.trainingBard)[arrayIndex]);
            tvResults.append("\n\n\n");

            // "For Love" result, use d12 instead of d20 on Romantic Relationship table later on
            if (arrayIndex == 4) {
                forLove = true;
            }
            // Criminal, roll on the Crime and Punishment Table, but only if we haven't already done so
            if (arrayIndex == 7) {
                if (!criminal) {
                    criminal = true;
                    crimeAndPunishmentTable();
                }
            }
        }
        else if ("Cavalier".equals(playerClass)) {
            tvResults.append(getResources().getTextArray(R.array.trainingCavalier)[0]);
            tvResults.append("\n");
            tvResults.append(getResources().getTextArray(R.array.trainingCavalier)[arrayIndex]);
            tvResults.append("\n\n\n");
        }
        else if ("Cleric".equals(playerClass)) {
            tvResults.append(getResources().getTextArray(R.array.trainingCleric)[0]);
            tvResults.append("\n");
            tvResults.append(getResources().getTextArray(R.array.trainingCleric)[arrayIndex]);
            tvResults.append("\n\n\n");

            // Criminal, roll on the Crime and Punishment Table, but only if we haven't already done so
            if (arrayIndex == 9) {
                if (!criminal) {
                    criminal = true;
                    crimeAndPunishmentTable();
                }
            }
        }
        else if ("Druid".equals(playerClass)) {
            tvResults.append(getResources().getTextArray(R.array.trainingDruid)[0]);
            tvResults.append("\n");
            tvResults.append(getResources().getTextArray(R.array.trainingDruid)[arrayIndex]);
            tvResults.append("\n\n\n");
        }
        else if ("Fighter".equals(playerClass)) {
            tvResults.append(getResources().getTextArray(R.array.trainingFighter)[0]);
            tvResults.append("\n");
            tvResults.append(getResources().getTextArray(R.array.trainingFighter)[arrayIndex]);
            tvResults.append("\n\n\n");
        }
        else if ("Gunslinger".equals(playerClass)) {
            tvResults.append(getResources().getTextArray(R.array.trainingGunslinger)[0]);
            tvResults.append("\n");
            tvResults.append(getResources().getTextArray(R.array.trainingGunslinger)[arrayIndex]);
            tvResults.append("\n\n\n");
        }
        else if ("Inquisitor".equals(playerClass)) {
            tvResults.append(getResources().getTextArray(R.array.trainingInquisitor)[0]);
            tvResults.append("\n");
            tvResults.append(getResources().getTextArray(R.array.trainingInquisitor)[arrayIndex]);
            tvResults.append("\n\n\n");
        }
        else if ("Magus".equals(playerClass)) {
            tvResults.append(getResources().getTextArray(R.array.trainingMagus)[0]);
            tvResults.append("\n");
            tvResults.append(getResources().getTextArray(R.array.trainingMagus)[arrayIndex]);
            tvResults.append("\n\n\n");
        }
        else if ("Monk".equals(playerClass)) {
            tvResults.append(getResources().getTextArray(R.array.trainingMonk)[0]);
            tvResults.append("\n");
            tvResults.append(getResources().getTextArray(R.array.trainingMonk)[arrayIndex]);
            tvResults.append("\n\n\n");
        }
        else if ("Oracle".equals(playerClass)) {
            tvResults.append(getResources().getTextArray(R.array.trainingOracle)[0]);
            tvResults.append("\n");
            tvResults.append(getResources().getTextArray(R.array.trainingOracle)[arrayIndex]);
            tvResults.append("\n\n\n");
        }
        else if ("Paladin".equals(playerClass)) {
            tvResults.append(getResources().getTextArray(R.array.trainingPaladin)[0]);
            tvResults.append("\n");
            tvResults.append(getResources().getTextArray(R.array.trainingPaladin)[arrayIndex]);
            tvResults.append("\n\n\n");
        }
        else if ("Ranger".equals(playerClass)) {
            tvResults.append(getResources().getTextArray(R.array.trainingRanger)[0]);
            tvResults.append("\n");
            tvResults.append(getResources().getTextArray(R.array.trainingRanger)[arrayIndex]);
            tvResults.append("\n\n\n");
        }
        else if ("Rogue".equals(playerClass)) {
            tvResults.append(getResources().getTextArray(R.array.trainingRogue)[0]);
            tvResults.append("\n");
            tvResults.append(getResources().getTextArray(R.array.trainingRogue)[arrayIndex]);
            tvResults.append("\n\n\n");
        }
        else if ("Sorcerer".equals(playerClass)) {
            tvResults.append(getResources().getTextArray(R.array.trainingSorcerer)[0]);
            tvResults.append("\n");
            tvResults.append(getResources().getTextArray(R.array.trainingSorcerer)[arrayIndex]);
            tvResults.append("\n\n\n");
        }
        else if ("Summoner".equals(playerClass)) {
            tvResults.append(getResources().getTextArray(R.array.trainingSummoner)[0]);
            tvResults.append("\n");
            tvResults.append(getResources().getTextArray(R.array.trainingSummoner)[arrayIndex]);
            tvResults.append("\n\n\n");
        }
        else if ("Witch".equals(playerClass)) {
            tvResults.append(getResources().getTextArray(R.array.trainingWitch)[0]);
            tvResults.append("\n");
            tvResults.append(getResources().getTextArray(R.array.trainingWitch)[arrayIndex]);
            tvResults.append("\n\n\n");
        }
        else if ("Wizard".equals(playerClass)) {
            tvResults.append(getResources().getTextArray(R.array.trainingWizard)[0]);
            tvResults.append("\n");
            tvResults.append(getResources().getTextArray(R.array.trainingWizard)[arrayIndex]);
            tvResults.append("\n\n\n");
        }
    }
    private void associatesTable() {
        d100 = ran.nextInt(100)+1;

        if (d100 < 6) {
            arrayIndex = 1;
        }
        else if (d100 < 11) {
            arrayIndex = 2;
        }
        else if (d100 < 16) {
            arrayIndex = 3;
        }
        else if (d100 < 21) {
            arrayIndex = 4;
        }
        else if (d100 < 26) {
            arrayIndex = 5;
        }
        // The Lover: roll a d12 instead of a d20 on the Romantic Relationships Table
        else if (d100 < 31) {
            arrayIndex = 6;
            forLove = true;
        }
        else if (d100 < 36) {
            arrayIndex = 7;
        }
        else if (d100 < 41) {
            arrayIndex = 8;
        }
        else if (d100 < 46) {
            arrayIndex = 9;
        }
        else if (d100 < 51) {
            arrayIndex = 10;
        }
        else if (d100 < 56) {
            arrayIndex = 11;
        }
        else if (d100 < 61) {
            arrayIndex = 12;
        }
        else if (d100 < 66) {
            arrayIndex = 13;
        }
        else if (d100 < 71) {
            arrayIndex = 14;
        }
        else if (d100 < 76) {
            arrayIndex = 15;
        }
        else if (d100 < 81) {
            arrayIndex = 16;
        }
        else if (d100 < 86) {
            arrayIndex = 17;
        }
        else if (d100 < 91) {
            arrayIndex = 18;
        }
        else if (d100 < 96) {
            arrayIndex = 19;
        }
        else {
            arrayIndex = 20;
        }

        tvResults.append(getResources().getTextArray(R.array.associates)[0]);
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.associates)[arrayIndex]);
        tvResults.append("\n\n\n");
    }

    // Moral Conflicts, Relationships, And Drawbacks Tables
    private void conflictsTable() {
        // Reset CP
        conflictPoints = 0;

        // Use d20 if any alignment, d12 if non-evil, d6 if good/paladin
        if ("Good Only".equals(allowedAlignments) || "Paladin".equals(playerClass)) {
            d20 = ran.nextInt(6)+1;
        }
        else if ("Non-Evil Only".equals(allowedAlignments)) {
            d20 = ran.nextInt(12)+1;
        }
        else {
            d20 = ran.nextInt(20)+1;
        }

        // No need for big if-else tables as results are 1:1 to the roll
        arrayIndex = d20;

        // Do need to lookup for Conflict Point (CP) values though!
        if (d20 < 5) {
            conflictPoints += 1;
        }
        else if (d20 < 8) {
            conflictPoints += 2;
        }
        else if (d20 < 10) {
            conflictPoints += 3;
        }
        else if (d20 < 12) {
            conflictPoints += 4;
        }
        else if (d20 < 14) {
            conflictPoints += 5;
        }
        else if (d20 < 18) {
            conflictPoints += 6;
        }
        else if (d20 == 18) {
            conflictPoints += 7;
        }
        else if (d20 == 19) {
            conflictPoints += 8;
        }
        else {
            conflictPoints += 12;
        }

        tvResults.append(getResources().getTextArray(R.array.conflicts)[0]);
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.conflicts)[arrayIndex]);
        tvResults.append("\n\n\n");
    }
    private void conflictSubjectTable() {
        d20 = ran.nextInt(20)+1;

        arrayIndex = d20;

        // Conflict with a child increase CP by 1
        if (d20 == 12) {
            conflictPoints += 1;
        }

        // Conflict with a lover makes you use a d12 instead of a d20 on the Romantic Relationships table
        if (d20 == 15) {
            forLove = true;
        }

        tvResults.append(getResources().getTextArray(R.array.conflictSubject)[0]);
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.conflictSubject)[arrayIndex]);
        tvResults.append("\n\n\n");
    }
    private void conflictMotivationTable() {
        // Use d10 if any alignment, d8 if non-evil, d4 if good/paladin
        if ("Good Only".equals(allowedAlignments) || "Paladin".equals(playerClass)) {
            d20 = ran.nextInt(4)+1;
        }
        else if ("Non-Evil Only".equals(allowedAlignments)) {
            d20 = ran.nextInt(8)+1;
        }
        else {
            d20 = ran.nextInt(10)+1;
        }

        // CP is d10 result / 2, rounded up. Rounding with Math.ceil() or similiar will involve floats and doubles
        // so we cheat to keep everything in nice clean ints, as int division by default rounds down
        conflictPoints += ((d20 + 1)/2);

        arrayIndex = d20;

        tvResults.append(getResources().getTextArray(R.array.conflictMotivation)[0]);
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.conflictMotivation)[arrayIndex]);
        tvResults.append("\n\n\n");
    }
    private void conflictResolutionTable() {
        // Use d7 if any alignment, d6 if non-evil, d4 if good/paladin
        if ("Good Only".equals(allowedAlignments) || "Paladin".equals(playerClass)) {
            d20 = ran.nextInt(4)+1;
        }
        else if ("Non-Evil Only".equals(allowedAlignments)) {
            d20 = ran.nextInt(6)+1;
        }
        else {
            d20 = ran.nextInt(7)+1;
        }

        // CP goes from -3 for 1 to +3 for 7 so...
        conflictPoints += d20 - 4;

        arrayIndex = d20;

        tvResults.append(getResources().getTextArray(R.array.conflictResolution)[0]);
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.conflictResolution)[arrayIndex]);
        tvResults.append("\n\n\n");
    }
    private void alignmentGenerator() {
        // Conflict Points (CP) are amassed by the previous conflict tables and distributed by the player/program across
        // atwo tracks, Law-Chaos and Good-Evil, that range from 1 to 9. 1-3 is Good/Law, 4-6 is Neutral, 7-9 Evil/Chaos.
        // Unlike standard generation we do this automatically and need to keep class and campaign alignment restrictions in mind.
        if (conflictPoints < 1) {
            conflictPoints = 1;
        }
        tvResults.append(getResources().getTextArray(R.array.alignmentGenerator)[0]);
        tvResults.append("\n");
        tvResults.append("(Your Conflict Points: " + conflictPoints +")");
        tvResults.append("\n\n");

        // Reset final alignments to false. We use an array of 9 bools to represent each alignment and whether it's valid
        for (int i = 0; i < finalAlignments.length; i++) {
            finalAlignments[i] = false;
        }

        // 1-3 CP: Lawful Good
        if (conflictPoints < 4) {
            finalAlignments[0] = true;
        }
        // 4-6 CP: Lawful Good, Neutral Good, Lawful Neutral
        else if (conflictPoints < 7) {
            finalAlignments[0] = true;
            finalAlignments[1] = true;
            finalAlignments[3] = true;
        }
        // 7 CP: Neutral Good, Chaotic Good, Lawful Neutral, Lawful Evil
        else if (conflictPoints < 8) {
            finalAlignments[1] = true;
            finalAlignments[2] = true;
            finalAlignments[3] = true;
            finalAlignments[6] = true;
        }
        // 8-9 CP: Neutral Good, Chaotic Good, Lawful Neutral, True Neutral, Lawful Evil
        else if (conflictPoints < 10) {
            finalAlignments[1] = true;
            finalAlignments[2] = true;
            finalAlignments[3] = true;
            finalAlignments[4] = true;
            finalAlignments[6] = true;
        }
        // 10 CP: Chaotic Good, True Neutral, Lawful Evil
        else if (conflictPoints < 11) {
            finalAlignments[2] = true;
            finalAlignments[4] = true;
            finalAlignments[6] = true;
        }
        // 11-12 CP: Chaotic Good, True Neutral, Chaotic Neutral, Lawful Evil, Neutral Evil
        else if (conflictPoints < 13) {
            finalAlignments[2] = true;
            finalAlignments[4] = true;
            finalAlignments[5] = true;
            finalAlignments[6] = true;
            finalAlignments[7] = true;
        }
        // 13 CP: Chaotic Neutral, Neutral Evil
        else if (conflictPoints < 14) {
            finalAlignments[5] = true;
            finalAlignments[7] = true;
        }
        // 14-15 CP: Chaotic Neutral, Neutral Evil, Chaotic Evil
        else if (conflictPoints < 16) {
            finalAlignments[5] = true;
            finalAlignments[7] = true;
            finalAlignments[8] = true;
        }
        // 16-18 CP: Chaotic Evil
        else {
            finalAlignments[8] = true;
        }

        // Campaign Restrictions
        if ("Good Only".equals(allowedAlignments)) {
            finalAlignments[3] = false;
            finalAlignments[4] = false;
            finalAlignments[5] = false;
            finalAlignments[6] = false;
            finalAlignments[7] = false;
            finalAlignments[8] = false;
        }
        else if ("Non-Evil Only".equals(allowedAlignments)) {
            finalAlignments[6] = false;
            finalAlignments[7] = false;
            finalAlignments[8] = false;
        }

        // Class Restrictions
        if (alignNeutral) {
            finalAlignments[0] = false;
            finalAlignments[2] = false;
            finalAlignments[6] = false;
            finalAlignments[8] = false;
        }
        if (alignGood) {
            finalAlignments[3] = false;
            finalAlignments[4] = false;
            finalAlignments[5] = false;
            finalAlignments[6] = false;
            finalAlignments[7] = false;
            finalAlignments[8] = false;
        }
        if (alignLawful) {
            finalAlignments[1] = false;
            finalAlignments[2] = false;
            finalAlignments[4] = false;
            finalAlignments[5] = false;
            finalAlignments[7] = false;
            finalAlignments[8] = false;
        }
        if (alignNonLawful) {
            finalAlignments[0] = false;
            finalAlignments[3] = false;
            finalAlignments[6] = false;
        }

        // By removing alignments due to class/campaign restrictions we may end up with no valid options printed
        // if this happens this variable will still be true even after the loop through the bools and we can fix it
        noValidAlignments = true;

        // Loop through, print allowed alignments
        for (int i = 0; i < finalAlignments.length; i++) {
            if (finalAlignments[i] == true) {
                noValidAlignments = false; // we have at least one allowed alignment
                tvResults.append(getResources().getTextArray(R.array.alignmentGenerator)[i+1] + "\n\n");
            }
        }

        // Too many CP generated to allow any of the alignments enforced by campaign/class, so we need to set a valid default alignment
        if (noValidAlignments == true) {
            //tvResults.append("DEBUG: NO VALID ALIGNMENTS, USING FALLBACK SYSTEM\n"); // DEBUG
            if ("Good Only".equals(allowedAlignments) || (alignGood == true) || conflictPoints < 4) {
                // LG: Paladins in any game, or Monks with Good-Only Campaign enabled
                if (alignLawful) {
                    tvResults.append(getResources().getTextArray(R.array.alignmentGenerator)[1] + "\n\n");
                }
                // NG: Druids with Good-Only Campaign enabled
                else if (alignNeutral) {
                    tvResults.append(getResources().getTextArray(R.array.alignmentGenerator)[2] + "\n\n");
                }
                // CG: Barbarians with Good-Only Campaign enabled
                else if (alignNonLawful) {
                    tvResults.append(getResources().getTextArray(R.array.alignmentGenerator)[3] + "\n\n");
                }
            }
            else if ("Non-Evil Only".equals(allowedAlignments) || conflictPoints < 10) {
                // LN: Monks with Non-Evil Campaign enabled
                if (alignLawful) {
                    tvResults.append(getResources().getTextArray(R.array.alignmentGenerator)[4] + "\n\n");
                }
                // TN: Druids with Non-Evil Campaign enabled
                else if (alignNeutral) {
                    tvResults.append(getResources().getTextArray(R.array.alignmentGenerator)[5] + "\n\n");
                }
                // CN: Barbarians with Non-Evil Campaign enabled
                else if (alignNonLawful) {
                    tvResults.append(getResources().getTextArray(R.array.alignmentGenerator)[6] + "\n\n");
                }
            }
            // Even without campaign restrictions, we've still buggered up
            else {
                // LE: Monks that are particularly Evil/Chaotic
                if (alignLawful) {
                    tvResults.append(getResources().getTextArray(R.array.alignmentGenerator)[7] + "\n\n");
                }
                // NE: Druids that are particularly Evil/Chaotic
                else if (alignNeutral) {
                    tvResults.append(getResources().getTextArray(R.array.alignmentGenerator)[8] + "\n\n");
                }
                // CN: Barbarians that are particularly Evil/Chaotic
                else if (alignNonLawful) {
                    tvResults.append(getResources().getTextArray(R.array.alignmentGenerator)[9] + "\n\n");
                }
            }
        }

        tvResults.append("\n");
    }
    private void deityAndReligion() {
        // Player has to choose religion/faith for themselves. Do display the options for No Deity and Undecided however
        tvResults.append(getResources().getTextArray(R.array.deityAndReligionInfo)[0]);
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.deityAndReligionInfo)[1]);
        tvResults.append("\n\n");
        tvResults.append(getResources().getTextArray(R.array.deityAndReligionInfo)[2]);
        tvResults.append("\n\n");
        tvResults.append(getResources().getTextArray(R.array.deityAndReligionInfo)[3]);
        tvResults.append("\n\n\n");
    }
    private void romanceTable() {
        // Use a d12 if character has a history with love and romance from other backgrounds
        if (forLove == true) {
            d20 = ran.nextInt(12)+1;
        }
        else {
            d20 = ran.nextInt(20)+1;
        }

        if (d20 < 3) {
            arrayIndex = 1;
        }
        else if (d20 < 7) {
            arrayIndex = 2;
        }
        else if (d20 < 10) {
            arrayIndex = 3;
        }
        else if (d20 < 13) {
            arrayIndex = 4;
        }
        else if (d20 < 17) {
            arrayIndex = 5;
        }
        else if (d20 < 19) {
            arrayIndex = 6;
        }
        else {
            arrayIndex = 7;
        }

        tvResults.append(getResources().getTextArray(R.array.romanticRelationships)[0]);
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.romanticRelationships)[arrayIndex]);
        tvResults.append("\n\n\n");
    }
    private void adventurerBondsTable() {
        // Original table is 1d100, but has 20 entries of equal probablility so let's just use a d20...
        arrayIndex = d20;
        d20 = ran.nextInt(20)+1;

        tvResults.append(getResources().getTextArray(R.array.adventurerBonds)[0]);
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.adventurerBonds)[arrayIndex]);
        tvResults.append("\n\n\n");
    }
    private void drawbackTable() {
        // Original table is 1d100, but has 20 entries of equal probablility so let's just use a d20...
        arrayIndex = d20;
        d20 = ran.nextInt(20)+1;

        tvResults.append(getResources().getTextArray(R.array.characterDrawback)[0]);
        tvResults.append("\n");
        tvResults.append(getResources().getTextArray(R.array.characterDrawback)[arrayIndex]);
        tvResults.append("\n\n\n");
    }


    //------------------------------------------------------------------------------------------//
    // Helper functions for table rolling not found in the original Ultimate Campaign generator //
    //------------------------------------------------------------------------------------------//

    // Random physical attributes - sourced from Core Rulebook, Advanced Race Guide and original content
    private void physicalSex() {
        // May eventually do something fancier with a sex/gender split and support for non-binary folks, but for now...simplicity
        int randomSex = 0;
        randomSex = ran.nextInt(2)+1;
        if (randomSex == 1) {
            apparentSex = "Male";
        }
        else {
            apparentSex = "Female";
        }
    }
    private void classAgeCategory() {
        // Intuitive Classes - youngest
        if ("Barbarian".equals(playerClass)||"Oracle".equals(playerClass)||"Rogue".equals(playerClass)||"Sorcerer".equals(playerClass)) {
            classAge = 1;
        }
        // Trained Classes - oldest
        else if ("Alchemists".equals(playerClass)||"Cleric".equals(playerClass)||"Druid".equals(playerClass)||"Inquisitor".equals(playerClass)||"Magus".equals(playerClass)||"Monk".equals(playerClass)||"Wizard".equals(playerClass)) {
            classAge = 3;
        }
        // Self-Taught Classes - largest group, so left until last to reduce needless string comparisons
        else {
            classAge = 2;
        }

    }
    private void racialAgeWeightHeight() {
        // Determines Age, Weight and Height for races, using the ARG Random Tables
        int numberOfDice, sizeOfDize; // no and type of dice to roll for age, weight, height
        int heightMod = 0, weightMult;

        if ("Dwarf".equals(playerRace)) {
            // Age
            characterAge = 40; // Adulthood is our starting age by default
            if (classAge == 1) {
                numberOfDice = 3; sizeOfDize = 6; // 3d6
            }
            else if (classAge == 2) {
                numberOfDice = 5; sizeOfDize = 6; // 5d6
            }
            else {
                numberOfDice = 7; sizeOfDize = 6; // 7d6
            }
            for (int i = 0; i < numberOfDice; i++) { // Rolls assigned no of n sided dice
                characterAge += ran.nextInt(sizeOfDize)+1;
            }

            // Height and Weight
            if (apparentSex == "Male") {
                heightFt = 3; heightIn = 9; weightLb = 150;
                numberOfDice = 2; sizeOfDize = 4; // 2d4
                weightMult = 7;
            }
            else {
                heightFt = 3; heightIn = 7; weightLb = 120;
                numberOfDice = 2; sizeOfDize = 4; // 2d4
                weightMult = 7;
            }
            for (int i = 0; i < numberOfDice; i++) {
                heightMod += ran.nextInt(sizeOfDize)+1;
            }
            heightIn += heightMod;
            weightLb += (heightMod * weightMult);
            while (heightIn > 12) { // 12 inches to a foot, so need to start converting to ft
                heightFt += 1;
                heightIn -= 12;
            }
        }

        else if ("Elf".equals(playerRace)) {
            // Age
            characterAge = 110; // Adulthood is our starting age by default
            if (classAge == 1) {
                numberOfDice = 4; sizeOfDize = 6; // 4d6
            }
            else if (classAge == 2) {
                numberOfDice = 6; sizeOfDize = 6; // 6d6
            }
            else {
                numberOfDice = 10; sizeOfDize = 6; // 10d6
            }
            for (int i = 0; i < numberOfDice; i++) { // Rolls assigned no of n sided dice
                characterAge += ran.nextInt(sizeOfDize)+1;
            }

            // Height and Weight
            if (apparentSex == "Male") {
                heightFt = 5; heightIn = 4; weightLb = 100;
                numberOfDice = 2; sizeOfDize = 8; // 2d8
                weightMult = 3;
            }
            else {
                heightFt = 5; heightIn = 4; weightLb = 90;
                numberOfDice = 2; sizeOfDize = 6; // 2d6
                weightMult = 3;
            }
            for (int i = 0; i < numberOfDice; i++) {
                heightMod += ran.nextInt(sizeOfDize)+1;
            }
            heightIn += heightMod;
            weightLb += (heightMod * weightMult);
            while (heightIn > 12) { // 12 inches to a foot, so need to start converting to ft
                heightFt += 1;
                heightIn -= 12;
            }
        }

        else if ("Gnome".equals(playerRace)) {
            // Age
            characterAge = 40; // Adulthood is our starting age by default
            if (classAge == 1) {
                numberOfDice = 4; sizeOfDize = 6; // 4d6
            }
            else if (classAge == 2) {
                numberOfDice = 6; sizeOfDize = 6; // 6d6
            }
            else {
                numberOfDice = 9; sizeOfDize = 6; // 9d6
            }
            for (int i = 0; i < numberOfDice; i++) { // Rolls assigned no of n sided dice
                characterAge += ran.nextInt(sizeOfDize)+1;
            }

            // Height and Weight
            if (apparentSex == "Male") {
                heightFt = 3; heightIn = 0; weightLb = 35;
                numberOfDice = 2; sizeOfDize = 4; // 2d4
                weightMult = 1;
            }
            else {
                heightFt = 2; heightIn = 10; weightLb = 30;
                numberOfDice = 2; sizeOfDize = 4; // 2d4
                weightMult = 1;
            }
            for (int i = 0; i < numberOfDice; i++) {
                heightMod += ran.nextInt(sizeOfDize)+1;
            }
            heightIn += heightMod;
            weightLb += (heightMod * weightMult);
            while (heightIn > 12) { // 12 inches to a foot, so need to start converting to ft
                heightFt += 1;
                heightIn -= 12;
            }
        }

        else if ("Half-Elf".equals(playerRace)) {
            // Age
            characterAge = 20; // Adulthood is our starting age by default
            if (classAge == 1) {
                numberOfDice = 1; sizeOfDize = 6; // 1d6
            }
            else if (classAge == 2) {
                numberOfDice = 2; sizeOfDize = 6; // 2d6
            }
            else {
                numberOfDice = 3; sizeOfDize = 6; // 3d6
            }
            for (int i = 0; i < numberOfDice; i++) { // Rolls assigned no of n sided dice
                characterAge += ran.nextInt(sizeOfDize)+1;
            }

            // Height and Weight
            if (apparentSex == "Male") {
                heightFt = 5; heightIn = 2; weightLb = 110;
                numberOfDice = 2; sizeOfDize = 8; // 2d8
                weightMult = 5;
            }
            else {
                heightFt = 5; heightIn = 0; weightLb = 90;
                numberOfDice = 2; sizeOfDize = 8; // 2d8
                weightMult = 5;
            }
            for (int i = 0; i < numberOfDice; i++) {
                heightMod += ran.nextInt(sizeOfDize)+1;
            }
            heightIn += heightMod;
            weightLb += (heightMod * weightMult);
            while (heightIn > 12) { // 12 inches to a foot, so need to start converting to ft
                heightFt += 1;
                heightIn -= 12;
            }
        }

        else if ("Half-Orc".equals(playerRace)) {
            // Age
            characterAge = 14; // Adulthood is our starting age by default
            if (classAge == 1) {
                numberOfDice = 1; sizeOfDize = 4; // 1d4
            }
            else if (classAge == 2) {
                numberOfDice = 1; sizeOfDize = 6; // 1d6
            }
            else {
                numberOfDice = 2; sizeOfDize = 6; // 2d6
            }
            for (int i = 0; i < numberOfDice; i++) { // Rolls assigned no of n sided dice
                characterAge += ran.nextInt(sizeOfDize)+1;
            }

            // Height and Weight
            if (apparentSex == "Male") {
                heightFt = 4; heightIn = 10; weightLb = 150;
                numberOfDice = 2; sizeOfDize = 12; // 2d12
                weightMult = 7;
            }
            else {
                heightFt = 4; heightIn = 5; weightLb = 110;
                numberOfDice = 2; sizeOfDize = 12; // 2d12
                weightMult = 7;
            }
            for (int i = 0; i < numberOfDice; i++) {
                heightMod += ran.nextInt(sizeOfDize)+1;
            }
            heightIn += heightMod;
            weightLb += (heightMod * weightMult);
            while (heightIn > 12) { // 12 inches to a foot, so need to start converting to ft
                heightFt += 1;
                heightIn -= 12;
            }
        }

        else if ("Halfling".equals(playerRace)) {
            // Age
            characterAge = 20; // Adulthood is our starting age by default
            if (classAge == 1) {
                numberOfDice = 2; sizeOfDize = 4; // 2d4
            }
            else if (classAge == 2) {
                numberOfDice = 3; sizeOfDize = 6; // 3d6
            }
            else {
                numberOfDice = 4; sizeOfDize = 6; // 4d6
            }
            for (int i = 0; i < numberOfDice; i++) { // Rolls assigned no of n sided dice
                characterAge += ran.nextInt(sizeOfDize)+1;
            }

            // Height and Weight
            if (apparentSex == "Male") {
                heightFt = 2; heightIn = 8; weightLb = 30;
                numberOfDice = 2; sizeOfDize = 4; // 2d4
                weightMult = 1;
            }
            else {
                heightFt = 2; heightIn = 6; weightLb = 25;
                numberOfDice = 2; sizeOfDize = 4; // 2d4
                weightMult = 1;
            }
            for (int i = 0; i < numberOfDice; i++) {
                heightMod += ran.nextInt(sizeOfDize)+1;
            }
            heightIn += heightMod;
            weightLb += (heightMod * weightMult);
            while (heightIn > 12) { // 12 inches to a foot, so need to start converting to ft
                heightFt += 1;
                heightIn -= 12;
            }
        }

        else if ("Human".equals(playerRace)) {
            // Age
            characterAge = 15; // Adulthood is our starting age by default
            if (classAge == 1) {
                numberOfDice = 1; sizeOfDize = 4; // 1d4
            }
            else if (classAge == 2) {
                numberOfDice = 1; sizeOfDize = 6; // 1d6
            }
            else {
                numberOfDice = 2; sizeOfDize = 6; // 2d6
            }
            for (int i = 0; i < numberOfDice; i++) { // Rolls assigned no of n sided dice
                characterAge += ran.nextInt(sizeOfDize)+1;
            }

            // Height and Weight
            if (apparentSex == "Male") {
                heightFt = 4; heightIn = 10; weightLb = 120;
                numberOfDice = 2; sizeOfDize = 10; // 2d10
                weightMult = 5;
            }
            else {
                heightFt = 4; heightIn = 5; weightLb = 85;
                numberOfDice = 2; sizeOfDize = 10; // 2d10
                weightMult = 5;
            }
            for (int i = 0; i < numberOfDice; i++) {
                heightMod += ran.nextInt(sizeOfDize)+1;
            }
            heightIn += heightMod;
            weightLb += (heightMod * weightMult);
            while (heightIn > 12) { // 12 inches to a foot, so need to start converting to ft
                heightFt += 1;
                heightIn -= 12;
            }
        }
    }
    private void physicalAttributes() {
        String sexStr, heightStr, weightStr; // helper vars for string manipulation
        tvResults.append(getResources().getTextArray(R.array.physicalDescription)[0]); // Print header
        tvResults.append("\n");

        // Sex & Age
        sexStr = getResources().getTextArray(R.array.physicalDescription)[1].toString(); // Need to insert variables into string resource to replace "%1$d" and other variable placeholder tokens
        tvResults.append(String.format(sexStr, apparentSex, characterAge));
        tvResults.append("\n"); // Add a newline seperately, as including it with a formatted append (italics, bold, etc) may strip formatting
        // Height
        heightStr = getResources().getTextArray(R.array.physicalDescription)[2].toString();
        tvResults.append(String.format(heightStr, heightFt, heightIn));
        tvResults.append("\n");
        // Weight
        weightStr = getResources().getTextArray(R.array.physicalDescription)[3].toString();
        tvResults.append(String.format(weightStr, weightLb));
        tvResults.append("\n");
    }
    private void eyesAndHair() {
        // Each race has an array of strings for their eye and hair colours to draw from
        String[] hairArray, eyeArray;

        if ("Dwarf".equals(playerRace)) {
            hairArray = getResources().getStringArray(R.array.dwarfHair);
            eyeArray = getResources().getStringArray(R.array.dwarfEyes);
        }
        else if ("Elf".equals(playerRace)) {
            hairArray = getResources().getStringArray(R.array.elfHair);
            eyeArray = getResources().getStringArray(R.array.elfEyes);
        }
        else if ("Gnome".equals(playerRace)) {
            hairArray = getResources().getStringArray(R.array.gnomeHair);
            eyeArray = getResources().getStringArray(R.array.gnomeEyes);
        }
        else if ("Half-Elf".equals(playerRace)) {
            hairArray = getResources().getStringArray(R.array.halfElfHair);
            eyeArray = getResources().getStringArray(R.array.halfElfEyes);
        }
        else if ("Half-Orc".equals(playerRace)) {
            hairArray = getResources().getStringArray(R.array.halfOrcHair);
            eyeArray = getResources().getStringArray(R.array.halfOrcEyes);
        }
        else if ("Halfling".equals(playerRace)) {
            hairArray = getResources().getStringArray(R.array.halflingHair);
            eyeArray = getResources().getStringArray(R.array.halflingEyes);
        }
        else {
            hairArray = getResources().getStringArray(R.array.humanHair);
            eyeArray = getResources().getStringArray(R.array.humanEyes);
        }

        // Randomly select from the arrays - currently doesn't bias the probabilities towards "normal" results
        String hairColour = hairArray[ran.nextInt(hairArray.length)];
        String eyeColour = eyeArray[ran.nextInt(eyeArray.length)];

        String appearanceStr = getResources().getTextArray(R.array.physicalDescription)[4].toString();
        tvResults.append(String.format(appearanceStr, hairColour, eyeColour));
        tvResults.append("\n\n\n");
    }

    //-------------//
    // GUI Helpers //
    //-------------//

    // Dialogs from actionbar menu in top-right
    private void showHelpDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);

        // setting custom layout to dialog
        dialog.setTitle(R.string.menuHelp);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // disable title
        dialog.setContentView(R.layout.dialog);

        // adding text dynamically
        TextView title = (TextView) dialog.findViewById(R.id.titleText);
        title.setText(R.string.menuHelp);
        TextView txt = (TextView) dialog.findViewById(R.id.dialogTextView);
        txt.setText(R.string.helpText);

        // adding button click event
        Button dismissButton = (Button) dialog.findViewById(R.id.dialogButton);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void showLegalDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);

        // setting custom layout to dialog
        dialog.setTitle(R.string.menuLegal);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // disable title
        dialog.setContentView(R.layout.dialog);

        // adding text dynamically
        TextView title = (TextView) dialog.findViewById(R.id.titleText);
        title.setText(R.string.menuLegal);
        TextView txt = (TextView) dialog.findViewById(R.id.dialogTextView);
        txt.setText(R.string.legalText);

        // adding button click event
        Button dismissButton = (Button) dialog.findViewById(R.id.dialogButton);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    //--------------------------------------------------------//
    // Save/Restore state upon rotations, layout changes, etc //
    //--------------------------------------------------------//

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putCharSequence("TextViewContent", tvResults.getText()); // save formatted text
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        tvResults.setText(savedInstanceState.getCharSequence("TextViewContent")); // restore formatted text
    }
}
