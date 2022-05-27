package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.List;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	private Graph<String, DefaultWeightedEdge> grafo;
	private EventsDao dao;
	private int sommaPeso;
	private List<String> ottimo;

	public Model() {
		dao = new EventsDao();
	}
	
	public void creaGrafo(String categoria, int mese) {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//Vertici
		Graphs.addAllVertices(grafo, dao.getVertici(categoria, mese));
		
		//Archi
		for(Adiacenza a : dao.getArchi(categoria, mese)) {
			Graphs.addEdgeWithVertices(grafo, a.getV1(), a.getV2(), a.getPeso());
			sommaPeso += a.getPeso();
		}
	}
	
	public List<Adiacenza> getArchiConPesoMaggiore() {
		double pesoMedio = sommaPeso/grafo.edgeSet().size();
		List<Adiacenza> archi = new ArrayList<>();
		
		for(DefaultWeightedEdge e : grafo.edgeSet()) {
			if(grafo.getEdgeWeight(e)>pesoMedio) {
				archi.add(new Adiacenza(grafo.getEdgeSource(e),
							grafo.getEdgeTarget(e), (int) grafo.getEdgeWeight(e)));
			}
		}
		
		return archi;
	}
	
	public List<String> calcolaPercorso(String sorgente, String destinazione) {
		ottimo = new ArrayList<>();
		List<String> parziale = new ArrayList<>();
		parziale.add(sorgente);
		cerca(parziale, destinazione);
		
		return ottimo;
	}

	private void cerca(List<String> parziale, String destinazione) {
		if(parziale.get(parziale.size()-1).equals(destinazione)) {
			if(ottimo.size()>parziale.size()) {
				ottimo = new ArrayList<>(parziale);
			}
			return;
		} 

		for(String s : Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1))) {
			if(!parziale.contains(s)) {
				parziale.add(s);
				cerca(parziale, destinazione);
				parziale.remove(parziale.size()-1);
			}
		}
		
		
	}
	
	

}