package com.crihexe.manager;

import java.util.List;

import com.crihexe.scraping.model.ScrapingRequest;

public class ScrapingManager {
	
	private RepositoryUpdater repo;
	
	private List<ScrapingRequest> requests;
	
	public ScrapingManager(RepositoryUpdater repo) throws Exception {
		this.repo = repo;
	}
	
	public void fetchScrapingRequests() {
		requests = repo.getScrapingRequests();
	}
	
	// deve creare una tabella o un db in cui categorizza le scraping request che il sito richiede
	// in base alla data e ne assegna un indice di priorita'. Ogni tot viene richiesto al sito
	// quali richieste ha, e la tabella verrà aggiornata man mano. In base alla priorità alcune
	// richieste verranno processate piu' o meno spesso, lasciando meno sospetti possibili a StockX
	// i cui eventuali pochi captcha verranno risolti tramite il CaptchaResolver.
	// verranno anche impiegati dei proxy per alcune richieste, sempre il base alla priorità.
	// man mano che la data di rilascio si avvicina, più la priorità si alzerà e più frequentemente
	// quella richiesta verrà soddisfatta. A questo punto quindi il sito dovrà richiedere tutti i
	// prodotti nel listino, tenendo sempre traccia dell'ultima volta che sono state soddisfatte le
	// richieste per ogni singolo prodotto.
	// eventuali prodotti che non vengono più mandati tramite le richieste del sito verranno
	// eliminati dalla tabella.
	// IMPORTANTE ricordarsi di non esagerare e fare meno richieste possibili a StockX.
	// le richieste che sono pronte per essere processate saranno passate al service in batch da
	// piu' richieste alla volta
	
}
