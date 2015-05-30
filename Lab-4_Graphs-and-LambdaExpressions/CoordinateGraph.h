/*
Lab 4 - Graphs and Lambda Expressions
Wolfgang C. Strack
Windows 8 Visual C++ 2013

This header file has the class definition of CoordinateGraph.
*/

#ifndef COORDINATE_GRAPH_H_
#define COORDINATE_GRAPH_H_

#include "Graph.h"
#include "Vertex.h"
#include "Edge.h"
#include "CoordinatePair.h"
#include <cmath>
#include <map>
using namespace std;

static const int ERADIUS = 6371; // Earth's radius in km
static const double PI = 3.141592653589;

class CoordinateGraph : Graph <CoordinatePair>
{
private:
	typedef pair<double, double> coordinates;
	typedef Vertex<CoordinatePair> Location;

	map<coordinates, Location> graph;

	// define functor
	struct degreesToRadians
	{
		degreesToRadians() {}
		double operator()(double degrees) { return (degrees * (PI / 180)); }
	};

	double haversine(pair<double, double> cp1, pair<double, double> cp2)
	{
		degreesToRadians d2r; // functor

		double dlat = d2r(cp2.first - cp1.first);
		double dlon = d2r(cp2.second - cp1.second);
		double a = pow(sin(dlat / 2.), 2.) +
				cos(d2r(cp1.first)) * cos(d2r(cp2.first)) *
				pow(sin(dlon / 2.), 2.);
		double c = 2. * atan2(sqrt(a), sqrt(1. - a));
		double distance = ERADIUS * c;

		return distance;
	}
public:
	// constructors and destructor
	CoordinateGraph()  {}
	~CoordinateGraph() {}
	// getters/setters
	const CoordinatePair & getLocation(const pair<double, double> &latitudeAndLongitude);
	// overloaded methods from Graph class
	bool add(CoordinatePair &cp1, CoordinatePair &cp2);
	bool remove(CoordinatePair &cp1, CoordinatePair &cp2);
	int getNumberOfVertices() const;
	int getNumberOfEdges() const;
	double getEdgeWeight(CoordinatePair &cp1, CoordinatePair &cp2);
	// other methods
	bool add(CoordinatePair &cpair);
	bool remove(CoordinatePair &cpair);
	// CoordinateGraph getKruskalMST();
	friend ostream & operator<<(ostream &os, const CoordinateGraph &cgraph);
};

const CoordinatePair & CoordinateGraph::getLocation(const pair<double, double> &latitudeAndLongitude)
{
	return graph[latitudeAndLongitude].getData();
}

bool CoordinateGraph::add(CoordinatePair &cp1, CoordinatePair &cp2)
{
	// Construct the vertices
	Location loc1(cp1);
	Location loc2(cp2);

	// get the map keys to insert the vertices
	auto coord1 = cp1.getCoordinates();
	auto coord2 = cp2.getCoordinates();

	// if any of the CoordinatePairs are already in the graph, return false
	if (graph.find(coord1) == graph.end() || graph.find(coord2) == graph.end())
	{
		return false;
	}

	// calculate edge weight
	auto edgeweight = haversine(coord1, coord2);

	// connect the two vertices "undirectedly"
	loc1.connect(loc2, edgeweight);
	loc2.connect(loc1, edgeweight);

	// insert the two vertices into the graph
	graph[coord1] = loc1;
	graph[coord2] = loc2;

	return true;
}

bool CoordinateGraph::remove(CoordinatePair &cp1, CoordinatePair &cp2)
{
	// get the coordinate pairs and vertices
	auto coord1 = cp1.getCoordinates();
	auto coord2 = cp2.getCoordinates();
	auto vert1 = graph[coord1];
	auto vert2 = graph[coord2];

	// disconnect the edges between the vertices
	vert1.disconnect(vert2);
	vert2.disconnect(vert1);

	// erase the vertices from the CoordinateGraph
	auto iter = graph.find(coord1);
	if (iter == graph.end())
	{
		return false;
	}
	graph.erase(iter);

	iter = graph.find(coord2);
	if (iter == graph.end())
	{
		return false;
	}
	graph.erase(iter);

	return true;
}

int CoordinateGraph::getNumberOfVertices() const
{
	return graph.size();
}

int CoordinateGraph::getNumberOfEdges() const
{
	int doubleEdges = 0;

	for (auto iter = graph.begin(); iter != graph.end(); ++iter)
	{
		doubleEdges += iter->second.getNumberOfEdges();
	}

	return (doubleEdges / 2);
}

double CoordinateGraph::getEdgeWeight(CoordinatePair &cp1, CoordinatePair &cp2)
{
	return graph[cp1.getCoordinates()].getEdgeWeight(graph[cp2.getCoordinates()]);
}

bool CoordinateGraph::add(CoordinatePair &cpair)
{
	Location loc(cpair);
	auto coord = cpair.getCoordinates();

	// if the CoordinatePair is already in the graph, return false
	if (graph.find(coord) == graph.end())
	{
		return false;
	}

	graph[coord] = loc;
	return true;
}

bool CoordinateGraph::remove(CoordinatePair &cpair)
{
	auto coord = cpair.getCoordinates();
	auto iter = graph.find(coord);

	// if CoordinatePair is not in the graph, return false
	if (iter == graph.end())
	{
		return false;
	}

	// disconnect all the edges connected to this vertex
	iter->second.disconnectAll();
	// remove vertex from graph
	graph.erase(iter);

	return true;
}

ostream & operator<<(ostream &os, const CoordinateGraph &cgraph)
{
	for (auto iter = cgraph.graph.begin(); iter != cgraph.graph.end(); iter++)
	{
		os << iter->second << endl;
	}

	return os;
}

#endif // COORDINATE_GRAPH_H_