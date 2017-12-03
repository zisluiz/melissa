// Agent sample_agent in project melissa

/* Initial beliefs and rules */
energia(100).
maxEnergia(100).
lifespan(45).
niceTemperature(25).
newBees(1).

/* Rules */

ta_quente(T) :-
	niceTemperature(NT) &
	T > NT + 1.

ta_frio(T) :-
	niceTemperature(NT) &
	T < NT - 1.
	
com_fome(E) :-
	maxEnergia(M) &
	E <= M * 0.2.
	
satisfeita(E) :-
	maxEnergia(M) &
	E > M * 0.9.
	
age_to_sentinel :-
	nascimento(N) &
	hoje(D) &
	D-N >= 18 & 
	.my_name(Me) & 
	play(Me, baba, _).
	
age_to_explorer :-
	nascimento(N) &
	hoje(D) &
	D-N >= 22 & 
	.my_name(Me) & 
	play(Me, sentinela, _).

too_old :-
	nascimento(N) &
	hoje(D) &
	D-N >= 45.

/* Initial goals */

/* Organisational Plans */

+obligation(Ag,Norm,committed(Ag,Mission,Scheme),Deadline)
    : .my_name(Ag)
   <- //.print("I am obliged to commit to ",Mission," on ",Scheme);
      commitMission(Mission)[artifact_name(Scheme)].

/*   Basic Plans  */
+!born
<- .print("I'm borning!");
+age(0);
joinWorkspace("colmeiaOrg",Workspace);
lookupArtifact("colmeia1",SchArtId);
focus(SchArtId);
adoptRole(baba);
commitMission(mBaba)[artifact_id(SchArtId)];
!registerBee[scheme(Sch)].

+!registerBee[scheme(Sch)] : age(X)
<-	lookupArtifact("Map",AId);
	focus(AId);
	?day(D)[artifact_id(AId)];
	+hoje(D);
	+nascimento(D-X);
	if (X < 18) {
		adoptRole(baba);
		registerBee(baba);	
	} else { if (X < 22) {
		adoptRole(sentinela);
		registerBee(sentinela);
	} else {
		adoptRole(exploradora);
		registerBee(exploradora);
		!setPosition;
	}};
	-age(_);
	!!updateDay.

+!registerBee[scheme(Sch)]
<-	lookupArtifact("Map",AId);
	focus(AId);
	?day(D)[artifact_id(AId)];
	+hoje(D);
	+nascimento(D);
	adoptRole(baba);
	registerBee(baba);
	!!updateDay. 

+!updateDay :hoje(H)
<-	.wait(5000);
	lookupArtifact("Map",AId);
	focus(AId);
	?day(D)[artifact_id(AId)];
	if (D \== H) {
		-+hoje(D)
	};
	!!changeStatus;
	!updateDay.

+!changeStatus : too_old
<-	.random(N);
	if(N < 0.5) {
		!suicide
	}.
	
+!changeStatus : age_to_explorer /* TEMP */& not role(explorer)
<-	changeRole(exploradora); 
	adoptRole(exploradora);
	removeRole(sentinela);
	-+role(explorer);		// TEMP - retirar apos consertar remocao de roles!!
	.print("Virei exploradora!").
	
+!changeStatus : age_to_sentinel /* TEMP */& not role(sentinel)
<-	changeRole(sentinela);
	adoptRole(sentinela);
	removeRole(baba);
	+role(sentinel);		// TEMP - retirar apos consertar remocao de roles!!
	.print("Virei sentinela!").

+!changeStatus.

+!setPosition
<-	lookupArtifact("Map",AId);
	focus(AId);
	?hive(X0,Y0,W,H)[artifact_id(AId)];
	.random(R1);
	X = X0 + math.floor(W*R1);
	.random(R2);
	Y = Y0 + math.floor(H*R2);
	setPosition(X,Y).

+!alimentarse: energia(E) & not satisfeita(E)
<-	comer(5);
	-+energia(E+10).

-!alimentarse <- .wait(100); !alimentarse.
+!alimentarse <- .wait(100); !alimentarse.

+energia(E) : E <= 0 <- !suicide.

+!suicide : .my_name(Me)
<- 	.print("Time to die");
	removeRole(exploradora);
	unRegisterBee;
	drop_all_intentions;
	-role(_);		// TEMP - retirar apos consertar remocao de roles!!
	ag_killed(Me).

/*   Baba Plans   */

+!fabricarMel : energia(E) & not com_fome(E)
<-	!tryPollen;	
	.wait(100);
	!fabricarMel;
	-+energia(E-1).

@tryPollen [atomic]
+!tryPollen
<- 	lookupArtifact("Hive",AId);
	focus(AId);
	if(pollen(P)[artifact_id(AId)] & P>1) {
		processPollen
	}.

-!fabricarMel
<-	.wait(500);
	!fabricarMel.

+!alimentarRainha : energia(E)
<-	.send(queen, achieve, comer(50));
	-+energia(E-1).

+!alimentarLarvas : newBees(SEQ) & .my_name(N)
<-  ?larvas(NR);
	if (NR > 0) {
		.print("Alimentando larvas");
		alimentarLarva(L);
		.concat(N, SEQ, NEWBEE);
		
		if (L) {
			.create_agent(NEWBEE,"worker.asl");
			.send(NEWBEE, achieve, born);
			+newBees(SEQ+1)
		};
	}
	
	.wait(300);
	!alimentarLarvas.
	
-!alimentarLarvas[error(ia_failed)] <- 
	//.print("Não consegui alimentar as larvas!");
	.wait(300);
	!alimentarLarvas.
-!alimentarLarvas[error_msg(M)] <- 
	//.print("Não consegui alimentar as larvas! Erro: ",M);
	.wait(300);
	!alimentarLarvas.	

/* Sentinel Plans */

+!aquecer : resfriando & energia(E) & not com_fome(E)
<- 	.wait(100+math.random(200));
	lookupArtifact("Hive",AId);
	focus(AId);
	if(intTemperature(T)[artifact_id(AId)] & ta_frio(T)) {
		stop_resfriar;
		-resfriando;
		-+energia(E-1)
	};
	!aquecer.

+!aquecer : not aquecendo & energia(E) & not com_fome(E)
<- 	.wait(100+math.random(200));
	lookupArtifact("Hive",AId);
	focus(AId);
	if(intTemperature(T)[artifact_id(AId)] & ta_frio(T)) {
		aquecer;
		+aquecendo;
		-+energia(E-1)
	};
	!aquecer.
	
+!aquecer <- .wait(100+math.random(200)); !aquecer.

+!resfriar: aquecendo & energia(E) & not com_fome(E)
<- 	.wait(100+math.random(200));
	lookupArtifact("Hive",AId);
	focus(AId);
	if(intTemperature(T)[artifact_id(AId)] & ta_quente(T)) {
		stop_aquecer;
		-aquecendo;
		-+energia(E-1)
	};
	!resfriar.

+!resfriar: not resfriando & energia(E) & not com_fome(E)
<- 	.wait(100+math.random(200));
	lookupArtifact("Hive",AId);
	focus(AId);
	if(intTemperature(T)[artifact_id(AId)] & ta_quente(T)) {
		resfriar;
		+resfriando;
		-+energia(E-1)
	};
	!resfriar;.
	
+!resfriar <- .wait(100+math.random(200)); !resfriar.

/* Explorer Plans */
	
+!procurarPolen[scheme(Sch)] : energia(E) & not com_fome(E)
<-	lookupArtifact("Map",AId);
	focus(AId);
	.findall(r(LEVEL, X, Y, WIDTH, HEIGHT), pollenField(LEVEL, X, Y, WIDTH, HEIGHT)[artifact_id(AId)], List);
	!flyToField(List);
	if(collect) {
		-collect;
		!coletarPolen[scheme(Sch)];
		!trazerPolen[scheme(Sch)]
	} else {
		!procurarPolen[scheme(Sch)]
	}.

+!procurarPolen[scheme(Sch)] <- .wait(100); !procurarPolen[scheme(Sch)].

-!procurarPolen[error(ia_failed)] <- .print("Não consegui procurar!").
-!procurarPolen[error_msg(M)]     <- .print("Error in: ",M).

+!flyToField([r(Lvl,X0,Y0,W,H)|L])
<-	.random(N);
	if (N < 0.2) {
		.random(R1);
		X = X0 + math.floor(W*R1);
		.random(R2);
		Y = Y0 + math.floor(H*R2);
		//.print("Going to (", X, ",",Y,")");
		flyTo(X,Y);
		+collect
	} else { if(not .empty(L)) {
		!flyToField(L)
	} else {
		
	}}.

+!trazerPolen[scheme(Sch)]
<-	!flyToHive;
	!estocarPolen[scheme(Sch)].

-!trazerPolen[error(ia_failed)] <- .print("I didn't in!").
-!trazerPolen[error_msg(M)]     <- .print("Error in: ",M).

+!flyToHive
<-	lookupArtifact("Map",AId);
	focus(AId);
	?hive(X0,Y0,W,H)[artifact_id(AId)];
	.random(R1);
	X = X0 + math.floor(W*R1);
	.random(R2);
	Y = Y0 + math.floor(H*R2);
	//.print("Going to (", X, ",",Y,")");
	flyTo(X,Y).

+!estocarPolen[scheme(Sch)] : energia(E)
<-	delivery;
	-+energia(E-20);
	!procurarPolen[scheme(Sch)].

-!estocarPolen[error(ia_failed)].
-!estocarPolen[error_msg(M)]/* <- .print("Error: ", M) */.

+!coletarPolen <- collect.	// arrumar aqui

-!coletarPolen[scheme(Sch),error_msg(M)]
<-	.print("Error in: ",M);
	!procurarPolen[scheme(Sch)].

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
{ include("$jacamoJar/templates/org-obedient.asl") }