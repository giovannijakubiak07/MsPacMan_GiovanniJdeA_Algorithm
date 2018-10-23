package pacman.controllers.examples;

import java.util.ArrayList;
import pacman.controllers.Controller;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;

import static pacman.game.Constants.*;

public class StarterPacMan_GiovanniJA extends Controller<MOVE> {

	private ArrayList<Node> arvore = new ArrayList<Node>();
	private Node noAnterior;
	public boolean iniciou = false;
	private int lastIndexArvore = 0;

	public MOVE getMove(Game game, long timeDue) {

		if (arvore.isEmpty()) {
			Node no = new Node();

			no.setNoAtual(game.getPacmanCurrentNodeIndex());
			no.setNoPai(null);
			no.setValorCaminhoAteAqui(1);
			no.setNivel(0);
			arvore = expandeNo(game, no);
		}

		int nextMove = arvore.get(0).getNoAtual();

		if (getDistanciaAteGhostMaisProximo(game, nextMove) <= 50) {
			arvore.clear();
		} else {
			arvore.remove(0);
		}
		return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), nextMove, DM.PATH);

	}

	public ArrayList<Node> expandeNo(Game game, Node no) {

		ArrayList<Node> nos = new ArrayList<Node>();

		no.setNosFilhos(null);

		int profundidadeLimite = 110;

		nos.add(no);

		Node currentNode = no;
		int index = 0;
		do {

			int[] PosicaoNodosFilhos;

			if (currentNode.getNoPai() == null) {
				PosicaoNodosFilhos = game.getNeighbouringNodes(currentNode.getNoAtual());
			} else {
				PosicaoNodosFilhos = game.getNeighbouringNodes(currentNode.getNoAtual(), game.getNextMoveTowardsTarget(
						currentNode.getNoPai().getNoAtual(), currentNode.getNoAtual(), DM.PATH));
			}

			ArrayList<Node> valorNosFilhos = new ArrayList<Node>();

			for (int j = 0; j < PosicaoNodosFilhos.length; j++) { // para cada filho

				int valorCaminho = Custo(game, PosicaoNodosFilhos, j); // seta custo do nó
				Node noFilho = new Node(currentNode, PosicaoNodosFilhos[j], null, 0); // cria no filho
				noFilho.setNivel(noFilho.getNoPai().getNivel() + 1);
				if (noFilho.getNoPai() != null) { // ve se ele tem pai, se tem
					valorCaminho = valorCaminho + noFilho.getNoPai().getValorCaminhoAteAqui();
					noFilho.setValorCaminhoAteAqui(valorCaminho);
				}
				nos.add(noFilho);
				valorNosFilhos.add(noFilho);
			}
			currentNode.setNosFilhos(valorNosFilhos);
			index++;
			Node noFilho = nos.get(index);

			currentNode = noFilho;
			// }
		} while (currentNode.getNivel() < profundidadeLimite);
		System.out.println(nos.size());
		nos = getMelhor(nos, profundidadeLimite);

		return nos;
	}

	public int Custo(Game game, int[] PosicaoNodosFilhos, int j) {
		int valorCaminho = 0;
		int ghostMaisProximo = getDistanciaAteGhostMaisProximo(game, PosicaoNodosFilhos[j]);
		if (ghostsParados(game) > 0) {
			valorCaminho = valorCaminho + 100 * ghostsParados(game);
		}
		if (ghostMaisProximo >= 900) {
			valorCaminho = valorCaminho + 300;
		}
		if (900 > ghostMaisProximo && ghostMaisProximo >= 500) {
			valorCaminho = valorCaminho + 250;
		}
		if (500 > ghostMaisProximo && ghostMaisProximo >= 300) {
			valorCaminho = valorCaminho + 200;
		}
		if (300 > ghostMaisProximo && ghostMaisProximo >= 150) {
			valorCaminho = valorCaminho + 170;
		}
		if (150 > ghostMaisProximo && ghostMaisProximo >= 100) {
			valorCaminho = valorCaminho + 140;
		}
		if (100 > ghostMaisProximo && ghostMaisProximo >= 80) {
			valorCaminho = valorCaminho + 120;
		}
		if (80 > ghostMaisProximo && ghostMaisProximo >= 60) {
			valorCaminho = valorCaminho + 100;
		}
		if (60 > ghostMaisProximo && ghostMaisProximo >= 40) {
			valorCaminho = valorCaminho + 80;
		}
		if (40 > ghostMaisProximo && ghostMaisProximo >= 30) {
			valorCaminho = valorCaminho + 60;
		}
		if (30 > ghostMaisProximo && ghostMaisProximo >= 20) {
			valorCaminho = valorCaminho + 30;
		}
		if (20 > ghostMaisProximo && ghostMaisProximo >= 10) {
			valorCaminho = valorCaminho + 10;

		}
		if (ghostMaisProximo < 10) {
			valorCaminho = valorCaminho + 1;

		}
		if (ghostMaisProximo == -1) {
			valorCaminho = valorCaminho + 1 + j;

		}
		if (PosicaoNodosFilhos[j] == getDistanciaAtePillMaisProxima(game, PosicaoNodosFilhos[j])) {
			valorCaminho = valorCaminho + 320;

		}
		if (PosicaoNodosFilhos[j] == getDistanciaAtePowerPillMaisProxima(game, PosicaoNodosFilhos[j])) {
			valorCaminho = valorCaminho + 450;

		}
		return valorCaminho;
	}

	public ArrayList<Node> getMelhor(ArrayList<Node> nos, int profundidadeLimite) {
		int maior = 0;

		for (int i = 0; i < nos.size(); i++) {

			if (nos.get(i).getNivel() == profundidadeLimite) {
				if (nos.get(i).getValorCaminhoAteAqui() > maior) {
					maior = nos.get(i).getValorCaminhoAteAqui();
				} else if (nos.get(i).getValorCaminhoAteAqui() == maior) {
					maior = nos.get(i).getValorCaminhoAteAqui() + ((int) Math.random() * 100);

				}
			}

		}
		Node melhor = new Node();
		for (int i = 0; i < nos.size(); i++) {

			if (nos.get(i).getValorCaminhoAteAqui() == maior) {
				melhor = nos.get(i);

			}

		}
		ArrayList<Node> best = new ArrayList<Node>();
		do {

			best.add(0, melhor);
			melhor = melhor.getNoPai();

		} while (melhor.getNoPai() != null);
		return best;
	}

	public int[] GhostsIndex(Game game) {

		int[] indexes = new int[4];

		indexes[0] = game.getGhostCurrentNodeIndex(GHOST.BLINKY);
		indexes[1] = game.getGhostCurrentNodeIndex(GHOST.INKY);
		indexes[2] = game.getGhostCurrentNodeIndex(GHOST.PINKY);
		indexes[3] = game.getGhostCurrentNodeIndex(GHOST.SUE);

		return indexes;
	}

	public int getDistanciaAteGhostMaisProximo(Game game, int posicao) {

		int[] ghostsPath = GhostsIndex(game);
		int menor = 9999999;
		for (int i = 0; i < ghostsPath.length; i++) {
			try {
				double distanciaAteGhostSelecionado = game.getDistance(posicao, ghostsPath[i], DM.PATH);
				if (distanciaAteGhostSelecionado < menor) {
					menor = (int) distanciaAteGhostSelecionado;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println(e);
			}
		}
		return menor;
	}

	public int getDistanciaAtePillMaisProxima(Game game, int posicao) {

		int[] PillsPath = game.getPillIndices();

		int pillMaisProxima = game.getClosestNodeIndexFromNodeIndex(posicao, PillsPath, DM.PATH);
		return pillMaisProxima;
	}

	public int getDistanciaAtePowerPillMaisProxima(Game game, int posicao) {

		int[] PillsPath = game.getPowerPillIndices();

		int pillMaisProxima = game.getClosestNodeIndexFromNodeIndex(posicao, PillsPath, DM.PATH);

		return pillMaisProxima;
	}

	public int ghostsParados(Game game) {

		int total = 0;

		int g1 = game.getGhostLairTime(GHOST.BLINKY);
		int g2 = game.getGhostLairTime(GHOST.INKY);
		int g3 = game.getGhostLairTime(GHOST.PINKY);
		int g4 = game.getGhostLairTime(GHOST.SUE);

		if (g1 > 0) {
			total += 1;
		}

		if (g2 > 0) {
			total += 1;
		}
		if (g3 > 0) {
			total += 1;
		}
		if (g4 > 0) {
			total += 1;
		}
		return total;
	}
	
	
}
class Node {
		
	/*
	 *
	 *No pai, no atual, nos filhos
	 *
	 */
	private Node noPai;
	private int noAtual;
	private ArrayList<Node> nosFilhos;
	private int valorCaminhoAteAqui;
	private int nivel;
	
	public Node() {}
	
	public Node(Node noPai, int noAtual, Node[] noFilhos, int valorCaminho) {
		
		this.noPai = noPai;
		this.noAtual = noAtual;
		this.nosFilhos = nosFilhos;
		this.valorCaminhoAteAqui = valorCaminho;
				
	}
	public Node(Node noPai, int noAtual, Node[] noFilhos, int valorCaminho, int nivel) {
		
		this.noPai = noPai;
		this.noAtual = noAtual;
		this.nosFilhos = nosFilhos;
		this.valorCaminhoAteAqui = valorCaminho;
		this.nivel = nivel;
				
	}
	
	
	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
	}

	public int getValorCaminhoAteAqui() {
		return valorCaminhoAteAqui;
	}

	public void setValorCaminhoAteAqui(int valorCaminhoAteAqui) {
		this.valorCaminhoAteAqui = valorCaminhoAteAqui;
	}

	public void addNoFilho(Node noFilho) {
		
		nosFilhos.add(noFilho);
	}

	public Node getNoPai() {
		return noPai;
	}

	public void setNoPai(Node noPai) {
		this.noPai = noPai;
	}

	public int getNoAtual() {
		return noAtual;
	}

	public void setNoAtual(int noAtual) {
		this.noAtual = noAtual;
	}

	public ArrayList<Node> getNosFilhos() {
		return nosFilhos;
	}

	public void setNosFilhos(ArrayList<Node> nosFilhos) {
		this.nosFilhos = nosFilhos;
	}
	
 
}
	 
