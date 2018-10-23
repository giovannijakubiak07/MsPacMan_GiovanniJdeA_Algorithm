package pacman.controllers.examples;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.sun.corba.se.impl.orbutil.graph.Node;
import com.sun.org.apache.bcel.internal.generic.GOTO;


import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

public class oiler extends Controller<MOVE> {
	public ArrayList<No> nos;
	public int cont;
	public int cont2 = 0;
	public int atual;
	public int pilulas[];
	public int powerpilulas[];

	public int destino;
	public MOVE move;
	public ArrayList<GHOST> ghosts;

	@Override
	public MOVE getMove(Game game, long timeDue) {
		move = null;
		ghosts = new ArrayList<GHOST>();

		for (GHOST ghost : GHOST.values()) {
			ghosts.add(ghost);

		}

		atual = construirArvore(game);

		verificaCaminho(game);
		GameView.addPoints(game, Color.green, game.getShortestPath(atual, destino));

		move = game.getNextMoveTowardsTarget(atual, destino, DM.PATH);
		

		return move;

	}

	private void verificaCaminho(Game game) {
		pilulas = game.getActivePillsIndices();
		powerpilulas = game.getActivePowerPillsIndices();
		boolean safe = true;
		cont2 = 0;

		for (No no : nos) {
			for (GHOST ghost : ghosts) {
				int indexGhost = game.getGhostCurrentNodeIndex(ghost);
				if (cont2 == 0) {
					if (game.getDistance(no.getIndex(), indexGhost, DM.EUCLID) < 30) {
						
						safe = false;
						if (!safe && powerpilulas.length > 0 && indexGhost > atual || indexGhost < atual) {
							int aux = 0;
							int tot = 0;
							for (int power : powerpilulas) {
								aux = atual - power;
								if (tot == 0 && aux > 0) {
									tot = aux;
								} else if (tot > aux && aux > 0) {
									tot = aux;
								}
							}
							destino = tot;
							
						} else {
							destino = pilulas[0];
						}

					} else if (safe) {
						
						for (int pill : pilulas) {
							if (pill != atual) {
								destino = pill;
							}
						}
					}
					cont2 = 1;
				}
				if (atual == destino) {
					cont2 = 0;
				}
			}

		}

	}

	private int construirArvore(Game game) {
		nos = new ArrayList<oiler.No>();
		cont = 0;
		atual = game.getPacmanCurrentNodeIndex();

		No oNo = new No(atual, 0, game.getPacmanLastMoveMade());
		nos.add(oNo);

		while (cont < 1000) {
			No primeiro = nos.remove(0);
			for (int index : game.getNeighbouringNodes(primeiro.getIndex())) {
				MOVE oMove = game.getMoveToMakeToReachDirectNeighbour(index, primeiro.getIndex()).opposite();
				this.nos.add(new No(index, primeiro, (primeiro.getNivel() + 1), oMove));

				cont++;

			}
		}
		return atual;

	}

	private ArrayList<No> orndenarNivel() {

		Collections.sort(nos, new Comparator() {
			@Override

			public int compare(Object arg0, Object arg1) {
				No no1 = (No) arg0;
				No no2 = (No) arg1;
				return no1.getNivel() > no2.getNivel() ? -1 : 1;
			}
		});

		return nos;
	}

	class No {

		private int index;
		private No pai;
		private int nivel;
		private MOVE oMove;

		public No(int index, int nivel, MOVE move) {
			this.index = index;
			this.nivel = nivel;
			this.oMove = move;
		}

		public No(int index, No pai, int nivel, MOVE move) {
			this.index = index;
			this.pai = pai;
			this.nivel = nivel;
			this.oMove = move;
		}

		public MOVE getoMove() {
			return oMove;
		}

		public void setoMove(MOVE oMove) {
			this.oMove = oMove;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public No getPai() {
			return pai;
		}

		public void setPai(No pai) {
			this.pai = pai;
		}

		public int getNivel() {
			return nivel;
		}

		public void setNivel(int nivel) {
			this.nivel = nivel;
		}

	}

}
