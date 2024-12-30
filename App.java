package MainApp;

import MainApp.WeightedGraph.Graph;
import MainApp.WeightedGraph.Vertex;
import MainApp.WeightedGraph.Edge;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.HashSet;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JFrame;

// Classe pour gérer l'affichage
class Board extends JComponent {
	private static final long serialVersionUID = 1L;
	Graph graph;
	int pixelSize;
	int ncols;
	int nlines;
	HashMap<Integer, String> colors;
	int start;
	int end;
	double max_distance;
	int current;
	LinkedList<Integer> path;

	public Board(Graph graph, int pixelSize, int ncols, int nlines, HashMap<Integer, String> colors, int start, int end) {
		super();
		this.graph = graph;
		this.pixelSize = pixelSize;
		this.ncols = ncols;
		this.nlines = nlines;
		this.colors = colors;
		this.start = start;
		this.end = end;
		this.max_distance = ncols * nlines;
		this.current = -1;
		this.path = null;
	}

	// Mise à jour de l'affichage
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// Effacement de la fenêtre
		g2.setColor(Color.cyan);
		g2.fill(new Rectangle2D.Double(0, 0, this.ncols * this.pixelSize, this.nlines * this.pixelSize));

		// --- Dessin des cases selon leur type/couleur
		int num_case = 0;
		for (Vertex v : this.graph.vertexlist) {
			double type = v.indivTime;
			int i = num_case / this.ncols;
			int j = num_case % this.ncols;

			// Sélection de la couleur en fonction de la table colors
			if (colors.get((int) type).equals("green"))
				g2.setPaint(Color.green);
			if (colors.get((int) type).equals("gray"))
				g2.setPaint(Color.gray);
			if (colors.get((int) type).equals("blue"))
				g2.setPaint(Color.blue);
			if (colors.get((int) type).equals("yellow"))
				g2.setPaint(Color.yellow);

			// Dessin de la case
			g2.fill(new Rectangle2D.Double(j * this.pixelSize, i * this.pixelSize, this.pixelSize, this.pixelSize));

			// Indication de la case courante en rouge
			if (num_case == this.current) {
				g2.setPaint(Color.red);
				g2.draw(new Ellipse2D.Double(j * this.pixelSize + this.pixelSize / 2,
						i * this.pixelSize + this.pixelSize / 2, 6, 6));
			}
			// Indication du départ
			if (num_case == this.start) {
				g2.setPaint(Color.white);
				g2.fill(new Ellipse2D.Double(j * this.pixelSize + this.pixelSize / 2,
						i * this.pixelSize + this.pixelSize / 2, 4, 4));
			}
			// Indication de l'arrivée
			if (num_case == this.end) {
				g2.setPaint(Color.black);
				g2.fill(new Ellipse2D.Double(j * this.pixelSize + this.pixelSize / 2,
						i * this.pixelSize + this.pixelSize / 2, 4, 4));
			}

			num_case += 1;
		}

		// --- Dessin de la "distance" (petits points gris selon timeFromSource)
		num_case = 0;
		for (Vertex v : this.graph.vertexlist) {
			int i = num_case / this.ncols;
			int j = num_case % this.ncols;
			if (v.timeFromSource < Double.POSITIVE_INFINITY) {
				float g_value = (float) (1 - v.timeFromSource / this.max_distance);
				if (g_value < 0)
					g_value = 0;
				g2.setPaint(new Color(g_value, g_value, g_value));
				g2.fill(new Ellipse2D.Double(j * this.pixelSize + this.pixelSize / 2,
						i * this.pixelSize + this.pixelSize / 2, 4, 4));
				Vertex previous = v.prev;
				if (previous != null) {
					int i2 = previous.num / this.ncols;
					int j2 = previous.num % this.ncols;
					g2.setPaint(Color.black);
					g2.draw(new Line2D.Double(j * this.pixelSize + this.pixelSize / 2,
							i * this.pixelSize + this.pixelSize / 2, j2 * this.pixelSize + this.pixelSize / 2,
							i2 * this.pixelSize + this.pixelSize / 2));
				}
			}
			num_case += 1;
		}

		// --- Dessin du chemin final en rouge
		int prev = -1;
		if (this.path != null) {
			g2.setStroke(new BasicStroke(3.0f));
			for (int cur : this.path) {
				if (prev != -1) {
					g2.setPaint(Color.red);
					int i = prev / this.ncols;
					int j = prev % this.ncols;
					int i2 = cur / this.ncols;
					int j2 = cur % this.ncols;
					g2.draw(new Line2D.Double(j * this.pixelSize + this.pixelSize / 2,
							i * this.pixelSize + this.pixelSize / 2, j2 * this.pixelSize + this.pixelSize / 2,
							i2 * this.pixelSize + this.pixelSize / 2));
				}
				prev = cur;
			}
		}
	}

	// Mise à jour du graphe (appelé pendant l'algorithme)
	public void update(Graph graph, int current) {
		this.graph = graph;
		this.current = current;
		repaint();
	}

	// Indiquer le chemin (pour affichage)
	public void addPath(Graph graph, LinkedList<Integer> path) {
		this.graph = graph;
		this.path = path;
		this.current = -1;
		repaint();
	}
}

// Classe principale
public class App {

	// Initialise l'affichage
	private static void drawBoard(Board board, int nlines, int ncols, int pixelSize) {
		JFrame window = new JFrame("Plus court chemin");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setBounds(0, 0, ncols * pixelSize + 20, nlines * pixelSize + 40);
		window.getContentPane().add(board);
		window.setVisible(true);
	}

	// --------------------------------------------------------------------------
	// Implémentation de l'algorithme Dijkstra
	// --------------------------------------------------------------------------
	private static LinkedList<Integer> Dijkstra(Graph graph, int start, int end, int numberV, Board board) {
		// Initialisation
		graph.vertexlist.get(start).timeFromSource = 0.0;
		int number_tries = 0;
		HashSet<Integer> to_visit = new HashSet<>();
		for (int i = 0; i < numberV; i++) {
			to_visit.add(i);
		}

		long startTime = System.currentTimeMillis();

		// Boucle principale
		while (to_visit.contains(end)) {
			// 1) Trouver le noeud min_v dont timeFromSource est minimal
			int min_v = -1;
			double minDist = Double.POSITIVE_INFINITY;
			for (int v : to_visit) {
				double dist = graph.vertexlist.get(v).timeFromSource;
				if (dist < minDist) {
					minDist = dist;
					min_v = v;
				}
			}
			// Cas où on ne trouve pas de chemin (min_v == -1)
			if (min_v == -1)
				break;

			to_visit.remove(min_v);
			number_tries++;

			// Si on est arrivé au noeud end, on peut sortir
			if (min_v == end)
				break;

			// 2) Mise à jour des voisins
			for (Edge e : graph.vertexlist.get(min_v).adjacencylist) {
				int to_try = e.destination;
				double alt = graph.vertexlist.get(min_v).timeFromSource + e.weight;
				if (alt < graph.vertexlist.get(to_try).timeFromSource) {
					graph.vertexlist.get(to_try).timeFromSource = alt;
					graph.vertexlist.get(to_try).prev = graph.vertexlist.get(min_v);
				}
			}

			// 3) Affichage intermédiaire
			try {
				board.update(graph, min_v);
				Thread.sleep(10);
			} catch (InterruptedException e) {
				System.out.println("stop");
			}
		}

		long endTime = System.currentTimeMillis();
		double totalTime = (endTime - startTime) / 1000.0; // en secondes

		System.out.println("Done! Using Dijkstra:");
		System.out.println("\tNumber of nodes explored: " + number_tries);
		System.out.println("\tTotal time of the path: " + graph.vertexlist.get(end).timeFromSource);
		System.out.println("\tAlgorithm runtime: " + totalTime + " s");

		// Reconstitution du chemin
		LinkedList<Integer> path = new LinkedList<>();
		path.addFirst(end);
		Vertex current = graph.vertexlist.get(end);
		while (current.prev != null) {
			current = current.prev;
			path.addFirst(current.num);
		}

		board.addPath(graph, path);
		return path;
	}

	// --------------------------------------------------------------------------
	// Implémentation de l'algorithme A*
	// --------------------------------------------------------------------------
	private static LinkedList<Integer> AStar(Graph graph, int start, int end, int ncols, int numberV, Board board) {
		// Initialisation
		graph.vertexlist.get(start).timeFromSource = 0.0;
		int number_tries = 0;
		HashSet<Integer> to_visit = new HashSet<>();
		for (int i = 0; i < numberV; i++) {
			to_visit.add(i);
		}

		// On calcule l'heuristique (Manhattan) pour chaque sommet
		for (int v = 0; v < numberV; v++) {
			int row_v = v / ncols;
			int col_v = v % ncols;
			int row_end = end / ncols;
			int col_end = end % ncols;
			graph.vertexlist.get(v).heuristic = Math.abs(row_v - row_end) + Math.abs(col_v - col_end);
		}

		long startTime = System.currentTimeMillis();

		// Boucle principale
		while (to_visit.contains(end)) {
			// 1) Trouver le noeud min_v dont (timeFromSource + heuristic) est minimal
			int min_v = -1;
			double min_f = Double.POSITIVE_INFINITY;
			for (int v : to_visit) {
				double f = graph.vertexlist.get(v).timeFromSource + graph.vertexlist.get(v).heuristic;
				if (f < min_f) {
					min_f = f;
					min_v = v;
				}
			}
			// Cas où aucun chemin n'est trouvé
			if (min_v == -1)
				break;

			to_visit.remove(min_v);
			number_tries++;

			// Si on a atteint end
			if (min_v == end)
				break;

			// 2) Mise à jour des voisins
			for (Edge e : graph.vertexlist.get(min_v).adjacencylist) {
				int to_try = e.destination;
				double alt = graph.vertexlist.get(min_v).timeFromSource + e.weight;
				if (alt < graph.vertexlist.get(to_try).timeFromSource) {
					graph.vertexlist.get(to_try).timeFromSource = alt;
					graph.vertexlist.get(to_try).prev = graph.vertexlist.get(min_v);
				}
			}

			// 3) Affichage intermédiaire
			try {
				board.update(graph, min_v);
				Thread.sleep(10);
			} catch (InterruptedException e) {
				System.out.println("stop");
			}
		}

		long endTime = System.currentTimeMillis();
		double totalTime = (endTime - startTime) / 1000.0; // en secondes

		System.out.println("Done! Using A*:");
		System.out.println("\tNumber of nodes explored: " + number_tries);
		System.out.println("\tTotal time of the path: " + graph.vertexlist.get(end).timeFromSource);
		System.out.println("\tAlgorithm runtime: " + totalTime + " s");

		// Reconstitution du chemin
		LinkedList<Integer> path = new LinkedList<>();
		path.addFirst(end);
		Vertex current = graph.vertexlist.get(end);
		while (current.prev != null) {
			current = current.prev;
			path.addFirst(current.num);
		}

		board.addPath(graph, path);
		return path;
	}

	// --------------------------------------------------------------------------
	// Méthode principale
	// --------------------------------------------------------------------------
	public static void main(String[] args) {
		try {
			// TODO: obtenir le fichier qui décrit la carte
			File myObj = new File("graph1.txt");  // <-- Chemin de votre fichier
			Scanner myReader = new Scanner(myObj);
			String data = "";

			// On ignore les trois premières lignes (selon votre fichier)
			for (int i = 0; i < 3; i++) {
				data = myReader.nextLine();
			}

			// Lecture du nombre de lignes
			int nlines = Integer.parseInt(data.split("=")[1]);
			// Et du nombre de colonnes
			data = myReader.nextLine();
			int ncols = Integer.parseInt(data.split("=")[1]);

			// Initialisation du graphe
			Graph graph = new Graph();

			HashMap<String, Integer> groundTypes = new HashMap<>();
			HashMap<Integer, String> groundColor = new HashMap<>();
			data = myReader.nextLine(); // =Types=
			data = myReader.nextLine(); // Première ligne de types ?

			// Lire les différents types de cases
			while (!data.equals("==Graph==")) {
				String name = data.split("=")[0];   // ex. "G"
				int time = Integer.parseInt(data.split("=")[1]); // ex. "1"
				data = myReader.nextLine();         // couleur
				String color = data;                // ex. "green"
				groundTypes.put(name, time);
				groundColor.put(time, color);

				data = myReader.nextLine(); // prochaine ligne
			}

			// On ajoute les sommets (cases) dans le graphe
			for (int line = 0; line < nlines; line++) {
				data = myReader.nextLine();
				for (int col = 0; col < ncols; col++) {
					int cost = groundTypes.get(String.valueOf(data.charAt(col)));
					graph.addVertex(cost);
				}
			}

			// TODO: ajouter les arêtes (ex. 8 voisins, ou 4 voisins selon)
			for (int line = 0; line < nlines; line++) {
				for (int col = 0; col < ncols; col++) {
					int source = line * ncols + col;
					// Exemples (4-voisins) :
					// Haut
					if (line > 0) {
						int dest = (line - 1) * ncols + col;
						double weight = (graph.vertexlist.get(source).indivTime +
								graph.vertexlist.get(dest).indivTime) / 2.0;
						graph.addEgde(source, dest, weight);
					}
					// Bas
					if (line < nlines - 1) {
						int dest = (line + 1) * ncols + col;
						double weight = (graph.vertexlist.get(source).indivTime +
								graph.vertexlist.get(dest).indivTime) / 2.0;
						graph.addEgde(source, dest, weight);
					}
					// Gauche
					if (col > 0) {
						int dest = line * ncols + (col - 1);
						double weight = (graph.vertexlist.get(source).indivTime +
								graph.vertexlist.get(dest).indivTime) / 2.0;
						graph.addEgde(source, dest, weight);
					}
					// Droite
					if (col < ncols - 1) {
						int dest = line * ncols + (col + 1);
						double weight = (graph.vertexlist.get(source).indivTime +
								graph.vertexlist.get(dest).indivTime) / 2.0;
						graph.addEgde(source, dest, weight);
					}
					// Diagonales (haut/gauche, haut/droite, bas/gauche, bas/droite), etc.
					// ...
				}
			}

			// Lecture des noeuds de départ et d'arrivée
			data = myReader.nextLine(); // ==Path==
			data = myReader.nextLine(); // Start= X,Y
			int startV = Integer.parseInt(data.split("=")[1].split(",")[0]) * ncols
					+ Integer.parseInt(data.split("=")[1].split(",")[1]);
			data = myReader.nextLine(); // Finish= X,Y
			int endV = Integer.parseInt(data.split("=")[1].split(",")[0]) * ncols
					+ Integer.parseInt(data.split("=")[1].split(",")[1]);

			myReader.close();

			// Affichage
			int pixelSize = 10;
			Board board = new Board(graph, pixelSize, ncols, nlines, groundColor, startV, endV);
			drawBoard(board, nlines, ncols, pixelSize);
			board.repaint();

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.out.println("stop");
			}

			// Choix de l'algorithme (menu)
			Scanner in = new Scanner(System.in);
			System.out.println("Choisissez l'algorithme de pathfinding : ");
			System.out.println("1. Dijkstra");
			System.out.println("2. A*");
			int choice = in.nextInt();

			LinkedList<Integer> path;
			if (choice == 1) {
				path = Dijkstra(graph, startV, endV, nlines * ncols, board);
			} else {
				path = AStar(graph, startV, endV, ncols, nlines * ncols, board);
			}
			in.close();

			// Écriture du chemin dans un fichier out.txt
			try {
				File file = new File("out.txt");
				if (!file.exists()) {
					file.createNewFile();
				}
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);

				for (int i : path) {
					bw.write(String.valueOf(i));
					bw.write('\n');
				}
				bw.close();
				System.out.println("Chemin écrit dans out.txt !");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
}
