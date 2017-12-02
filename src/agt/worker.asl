// Agent sample_agent in project melissa

/* Initial beliefs and rules */
energia(100).
lifespan(45).
niceTemperature(25).

/* Rules */

ta_quente(T) :-
	niceTemperature(NT) &
	T > NT + 1.

ta_frio(T) :-
	niceTemperature(NT) &
	T < NT - 1.
	

/* Initial goals */

/* Organisational Plans */

+obligation(Ag,Norm,committed(Ag,Mission,Scheme),Deadline)
    : .my_name(Ag)
   <- //.print("I am obliged to commit to ",Mission," on ",Scheme);
      commitMission(Mission)[artifact_name(Scheme)].

/*   Basic Plans  */

+!registerBee[scheme(Sch)]
<-	.my_name(N);
	?play(N,R,colmeia);
	if (R == baba) {
		registerBee(feeder);	
	} else {
		if (R == sentinela) {
			registerBee(sentinel);	
		} else {
			registerBee(worker);
			!setPosition
		}			
	}.

+!setPosition
<-	lookupArtifact("Map",AId);
	focus(AId);
	?hive(X0,Y0,W,H)[artifact_id(AId)];
	.random(R1);
	X = X0 + math.floor(W*R1);
	.random(R2);
	Y = Y0 + math.floor(H*R2);
	setPosition(X,Y).

+!alimentarse.

/*   Baba Plans   */

+!fabricarMel
<-	!tryPollen;	
	.wait(100);
	!fabricarMel.

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

+!alimentarRainha.

+!alimentarLarvas.

/* Sentinel Plans */

+!aquecer : resfriando
<- 	.wait(100+math.random(200));
	lookupArtifact("Hive",AId);
	focus(AId);
	if(intTemperature(T)[artifact_id(AId)] & ta_frio(T)) {
		stop_resfriar;
		-resfriando
	};
	!aquecer.

+!aquecer : not aquecendo
<- 	.wait(100+math.random(200));
	lookupArtifact("Hive",AId);
	focus(AId);
	if(intTemperature(T)[artifact_id(AId)] & ta_frio(T)) {
		aquecer;
		+aquecendo
	};
	!aquecer.
	
+!aquecer <- .wait(100+math.random(200)); !aquecer.

+!resfriar: aquecendo
<- 	.wait(100+math.random(200));
	lookupArtifact("Hive",AId);
	focus(AId);
	if(intTemperature(T)[artifact_id(AId)] & ta_quente(T)) {
		stop_aquecer;
		-aquecendo
	};
	!resfriar.

+!resfriar: not resfriando
<- 	.wait(100+math.random(200));
	lookupArtifact("Hive",AId);
	focus(AId);
	if(intTemperature(T)[artifact_id(AId)] & ta_quente(T)) {
		resfriar;
		+resfriando
	};
	!resfriar.
	
+!resfriar <- .wait(100+math.random(200)); !resfriar.

/* Explorer Plans */
	
+!procurarPolen[scheme(Sch)]
<-	lookupArtifact("Map",AId);
	focus(AId);
	.findall(r(LEVEL, X, Y, WIDTH, HEIGHT), pollenField(LEVEL, X, Y, WIDTH, HEIGHT)[artifact_id(AId)], List);
	!flyToField(List);
	if(collect) {
		-collect;
		!coletarPolen;
		!trazerPolen[scheme(Sch)]
	} else {
		!procurarPolen[scheme(Sch)]
	}.

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

+!estocarPolen[scheme(Sch)] <-
	delivery;
	!procurarPolen[scheme(Sch)].

-!estocarPolen[error(ia_failed)].
-!estocarPolen[error_msg(M)]/* <- .print("Error: ", M) */.

+!coletarPolen <- collect.	// arrumar aqui

-!coletarPolen[error_msg(M)]
<-	.print("Error in: ",M);
	!flyToHive.

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
{ include("$jacamoJar/templates/org-obedient.asl") }