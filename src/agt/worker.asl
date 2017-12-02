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
			setPosition(math.round(761+math.random(30)), 449); /*esse comando serve apenas pra minha lógica de subir e descer funcione, 
			quando elas se registram, elas já são posicionadas dentro da colmeia em pontos aleatorios*/
		}			
	}.

+!alimentarse.

/*   Baba Plans   */

+!fabricarMel
<-	!tryPolen;	
	.wait(100);
	!fabricarMel.

@tryPolen [atomic]
+!tryPolen
<- 	lookupArtifact("Hive",AId);
	focus(AId);
	if(polen(P)[artifact_id(AId)] & P>1) {
		processPolen;
		//.print("Polen processado!")
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
	
	.findall(r(X, Y, WIDTH, HEIGHT), pollenField(X, Y, WIDTH, HEIGHT)[artifact_id(AId)], List);
	
	!collectHoney;
	!trazerPolen[scheme(Sch)].

-!procurarPolen[error(ia_failed)] <- .print("Não consegui procurar!").
-!procurarPolen[error_msg(M)]     <- .print("Error in: ",M).


+!trazerPolen[scheme(Sch)]
   <- lookupArtifact("Hive",AId);
      focus(AId);
    ?pollenField(pollenField4, X, Y, WIDTH, HEIGHT);
	for ( .range(J,Y + HEIGHT, 450)) {
		move(down);
	}
	
	!estocarPolen[scheme(Sch)].

-!trazerPolen[error(ia_failed)] <- .print("I didn't in!").
-!trazerPolen[error_msg(M)]     <- .print("Error in: ",M).

+!estocarPolen[scheme(Sch)] <-
	!delivery;
	!procurarPolen[scheme(Sch)].

-!estocarPolen[error(ia_failed)].
-!estocarPolen[error_msg(M)]/* <- .print("Error: ", M) */.

+!collectHoney <- collect(pollenField4).
+!delivery <- delivery.

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
{ include("$jacamoJar/templates/org-obedient.asl") }