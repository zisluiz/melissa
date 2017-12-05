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
	role(baba).
	
age_to_explorer :-
	nascimento(N) &
	hoje(D) &
	D-N >= 22 & 
	role(sentinela).

too_old :-
	nascimento(N) &
	hoje(D) &
	D-N >= 45 &
	role(exploradora).

/* Initial goals */

/* Organisational Plans */

+obligation(Ag,Norm,committed(Ag,Mission,Scheme),Deadline)
    : .my_name(Ag)
   <- //.print("I am obliged to commit to ",Mission," on ",Scheme);
      commitMission(Mission)[artifact_name(Scheme)].
      
/*   Basic Plans  */
+!born
<-	//.print("I'm borning!"); // Frase mais estranha possível...
	.abolish(nascimento(_));
	.abolish(age(_));
	joinWorkspace("colmeiaOrg",Workspace);
	lookupArtifact("colmeia1",SchArtId);
	focus(SchArtId);
	!!registerBee.

+!registerBee : age(X)
<-	.random(N);
	lookupArtifact("Map",AId);
	focus(AId);
	?day(D)[artifact_id(AId)];
	+hoje(D);
	if (X < 18) {
		+nascimento(D-math.floor(18*N));
		adoptRole(baba);
		commitMission(mBaba);
		registerBee(baba);
		+role(baba)
	} else { if (X < 22) {
		+nascimento(D-(18+math.floor(4*N)));
		adoptRole(sentinela);
		commitMission(mSentinela);
		registerBee(sentinela);
		+role(sentinela)
	} else {
		+nascimento(D-(22+math.floor(23*N)));
		adoptRole(exploradora);
		commitMission(mExploradora);
		registerBee(exploradora);
		+role(exploradora)
	}};
	-age(_);
	!!updateDay.

+!registerBee
<-	lookupArtifact("Map",AId);
	focus(AId);
	?day(D)[artifact_id(AId)];
	+hoje(D);
	+nascimento(D);
	adoptRole(baba);
	commitMission(mBaba);
	registerBee(baba);
	+role(baba);
	!!startBaba. 

+!startBaba
<-	.drop_all_intentions;
	!!alimentarLarvas;
	!!alimentarRainha;
	!!alimentarse;
	!!fabricarMel;
	!!alimentarse;
	!!updateDay.

+!startSentinela
<-	.drop_all_intentions;
	!!aquecer;
	!!resfriar;
	!!alimentarse;
	!!updateDay.

+!startExploradora
<-	.drop_all_intentions;
	!!stopAquecerResfriar;
	!!procurarPolen;
	!!alimentarse;
	!!updateDay.

+!updateDay : hoje(H)
<-	.wait(5000);
	lookupArtifact("Map",AId);
	focus(AId);
	?day(D)[artifact_id(AId)];
	if (D \== H) {
		-+hoje(D);
		?energia(E);
		-+energia(E-1);
		!!changeStatus;
	};
	!updateDay.

+!changeStatus : too_old
<-	.random(N);
	if(N < 0.5) {
		!!suicide;
	}.
	
+!changeStatus : age_to_explorer
<-	changeRole(exploradora); 
	leaveMission(mSentinela);
	removeRole(sentinela);
	adoptRole(exploradora);
	commitMission(mExploradora);
	-role(sentinela);
	+role(exploradora);		// TEMP - retirar apos consertar remocao de roles!!
	!startExploradora.
	
+!changeStatus : age_to_sentinel
<-	changeRole(sentinela);
	leaveMission(mBaba);
	removeRole(baba);
	adoptRole(sentinela);
	commitMission(mSentinela);
	-role(baba);
	+role(sentinela);		// TEMP - retirar apos consertar remocao de roles!!
	!startSentinela.

+!changeStatus.

+!suicide : .my_name(Me)
<- 	.findall(Mission,commit(Me,Mission,_),M)
	if (not .empty(M)) {
		!leaveMission(M);
	};
	.findall(Role,play(Me,Role,_),R)
	if (not .empty(R)) {
		!removeRole(R);
	};
	unRegisterBee;
	.kill_agent(Me).
	
-!suicide <- .wait(500); !suicide.

+!leaveMission([M|R])
<-	leaveMission(M);
	if(not .empty(R)) {
		!leaveMission(R)
	}.

+!removeRole([Role|R])
<-	removeRole(Role);
	if(not .empty(R)) {
		!removeRole(R)
	}.

+!alimentarse: energia(E) & not satisfeita(E)
<-	comer(1);
	-+energia(E+10).

-!alimentarse <- .wait(100); !!alimentarse.
+!alimentarse <- .wait(100); !!alimentarse.

+energia(E) : E <= 0 <- !suicide.

/*   Baba Plans   */

+!fabricarMel : energia(E) & not com_fome(E) & role(baba)
<-	!tryPollen;	
	.wait(100);
	-+energia(E-1);
	!!fabricarMel.
	
+!fabricarMel <- .wait(100); !fabricarMel.

-!fabricarMel
<-	.wait(500);
	!fabricarMel.

+!tryPollen
<- 	lookupArtifact("Hive",AId);
	focus(AId);
	if(pollen(P)[artifact_id(AId)] & P>1) {
		processPollen;
	}.

+!alimentarRainha : energia(E) & role(baba)
<-	.send(queen, achieve, comer(50));
	-+energia(E-1).

+!alimentarRainha.

+!alimentarLarvas : role(baba)
<-  lookupArtifact("Hive",AId);
	focus(AId);
	?larvas(NR);
	if (NR > 0) {
		//.print("Feeding Larva");
		alimentarLarva(L);
		if (L) {
			!!evolveLarva;
		}
	}
	.wait(300);
	!!alimentarLarvas[scheme(Sch)].

+!alimentarLarvas.	
	
-!alimentarLarvas[error(ia_failed)] <- 
	.print("Não consegui alimentar as larvas!");
	.wait(300);
	!!alimentarLarvas[scheme(Sch)].
-!alimentarLarvas[error_msg(M)] <- 
	//.print("Não consegui alimentar as larvas! Erro: ",M);
	.wait(300);
	!!alimentarLarvas[scheme(Sch)].	
	
//-!alimentarLarvas <- .wait(300); !!alimentarLarvas.

+!evolveLarva : newBees(SEQ) & .my_name(N) & role(baba)
<- //.print("Larva is evolving");
   .concat(N, "_new", NSUFIX);
   .concat(NSUFIX, SEQ, NEWBEE);
   .create_agent(NEWBEE,"worker.asl");
   .send(NEWBEE, achieve, born);
   -+newBees(SEQ+1).
   
+!evolveLarva.

/* Sentinel Plans */

+!aquecer : resfriando & energia(E) & not com_fome(E) & role(sentinela)
<- 	.wait(100+math.random(200));
	lookupArtifact("Hive",AId);
	focus(AId);
	if(intTemperature(T)[artifact_id(AId)] & ta_frio(T)) {
		stop_resfriar;
		-resfriando;
		-+energia(E-1)
	};
	!!aquecer.

+!aquecer : not aquecendo & energia(E) & not com_fome(E) & role(sentinela)
<- 	.wait(100+math.random(200));
	lookupArtifact("Hive",AId);
	focus(AId);
	if(intTemperature(T)[artifact_id(AId)] & ta_frio(T)) {
		aquecer;
		+aquecendo;
		-+energia(E-1)
	};
	!!aquecer.
	
+!aquecer : role(sentinela) <- .wait(100+math.random(200)); !!aquecer.

+!aquecer.

+!resfriar: aquecendo & energia(E) & not com_fome(E) & role(sentinela)
<- 	.wait(100+math.random(200));
	lookupArtifact("Hive",AId);
	focus(AId);
	if(intTemperature(T)[artifact_id(AId)] & ta_quente(T)) {
		stop_aquecer;
		-aquecendo;
		-+energia(E-1)
	};
	!!resfriar.

+!resfriar: not resfriando & energia(E) & not com_fome(E) & role(sentinela)
<- 	.wait(100+math.random(200));
	lookupArtifact("Hive",AId);
	focus(AId);
	if(intTemperature(T)[artifact_id(AId)] & ta_quente(T)) {
		resfriar;
		+resfriando;
		-+energia(E-1)
	};
	!!resfriar.
	
+!resfriar : role(sentinela)<- .wait(100+math.random(200)); !!resfriar.

+!resfriar.

+!stopAquecerResfriar : role(exploradora) & aquecendo
<- stop_aquecer; -aquecendo; !stopAquecerResfriar.

+!stopAquecerResfriar : role(exploradora) & resfriando
<- stop_resfriar; -resfriando; !stopAquecerResfriar.

+!stopAquecerResfriar.

/* Explorer Plans */
	
+!procurarPolen : energia(E) & not com_fome(E) & role(exploradora)
<-	lookupArtifact("Map",AId);
	focus(AId);
	.findall(r(X, Y, WIDTH, HEIGHT), pollenField(_, X, Y, WIDTH, HEIGHT)[artifact_id(AId)], List);
	!flyToField(List);
	if(collect) {
		-collect;
		!coletarPolen;
		!trazerPolen
	} else {
		!!procurarPolen
	}.

+!procurarPolen : role(exploradora) <- .wait(100); !!procurarPolen.
+!procurarPolen.

-!procurarPolen[error(ia_failed)] <- .print("Não consegui procurar!").
-!procurarPolen[error_msg(M)]     <- .print("Error procurarPolen in: ",M); !!procurarPolen.

+!flyToField([r(X0,Y0,W,H)|L])
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

+!trazerPolen : role(exploradora)
<-	!flyToHive;
	!estocarPolen.
	
+!trazerPolen.

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

+!estocarPolen : energia(E) & role(exploradora)
<-	delivery;
	-+energia(E-10);
	!!procurarPolen.

+!estocarPolen.

-!estocarPolen[error(ia_failed)].
-!estocarPolen[error_msg(M)]/* <- .print("Error: ", M) */.

+!coletarPolen : role(exploradora) <- collect.

+!coletarPolen.

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
{ include("$jacamoJar/templates/org-obedient.asl") }