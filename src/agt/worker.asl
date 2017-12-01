// Agent sample_agent in project melissa

/* Initial beliefs and rules */
// x, y, width, height
hive(649, 449, 150, 150).

pollenField1(0, 0, 150, 200).
pollenField2(0, 300, 60, 170).
pollenField3(400, 0, 40, 40).
pollenField4(759, 230, 40, 40).

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
		registerBee(feeder, 10);	
	} else {
		if (R == sentinela) {
			registerBee(sentinel, 20);	
		} else {
			registerBee(worker, 50);
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
		.print("Polen processado!")
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
<-	lookupArtifact("Hive",AId);
	focus(AId);
	
	X = math.floor(math.random(Width));
	Y = math.floor(math.random(Heigth));
	move(X,Y);
	
    /*  
	for ( .range(I,Y + HEIGHT, 450)) {
		move(up);
	}
	*/
	
	!collectHoney;
	!trazerPolen[scheme(Sch)].

-!procurarPolen[error(ia_failed)] <- .print("Não consegui procurar!").
-!procurarPolen[error_msg(M)]     <- .print("Error in: ",M).


+!trazerPolen[scheme(Sch)] : pollenField4(X, Y, WIDTH, HEIGHT)
   <- lookupArtifact("Hive",AId);
      focus(AId);
      
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