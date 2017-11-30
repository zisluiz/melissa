// Agent sample_agent in project melissa

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start 
<-  .wait(3000); 
	registerBee(queen, 100); 
	honeyStart(100);
	!startOrg;
	!createLarva.

+!startOrg
<-	// creates a scheme to coordinate the auction
	makeArtifact("sobreviver", "ora4mas.nopl.SchemeBoard",["src/org/organisation.xml", sobreviver],SchArtId);
	//debug(inspector_gui(on))[artifact_id(SchArtId)];
	//setArgumentValue(auction,"Id",Id)[artifact_id(SchArtId)];
	.my_name(Me); setOwner(Me)[artifact_id(SchArtId)];  // I am the owner of this scheme!
	focus(SchArtId);
	addScheme("sobreviver");  // set the group as responsible for the scheme
	adoptRole(Monarca);.
	//commitMission(mRainha)[artifact_id(SchArtId)].

+!createLarva 
<-	createLarva;
	.wait(1000);
	!createLarva.

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
{ include("$jacamoJar/templates/org-obedient.asl") }