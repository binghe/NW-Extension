// (C) Uri Wilensky. https://github.com/NetLogo/NW-Extension

package org.nlogo.extensions.nw

import org.nlogo.agent.Link
import org.nlogo.agent.TreeAgentSet
import org.nlogo.agent.Turtle
import org.nlogo.extensions.nw.NetworkExtensionUtil.AgentSetToRichAgentSet

class GraphContext(
  private val turtleSet: TreeAgentSet,
  private val linkSet: TreeAgentSet) {

  def asJungGraph: jung.Graph = if (isDirected) asDirectedJungGraph else asUndirectedJungGraph
  def asDirectedJungGraph: jung.DirectedGraph = new jung.DirectedGraph(this)
  def asUndirectedJungGraph: jung.UndirectedGraph = new jung.UndirectedGraph(this)

  def asJGraphTGraph: jgrapht.Graph = if (isDirected) asDirectedJGraphTGraph else asUndirectedJGraphTGraph
  def asDirectedJGraphTGraph = new jgrapht.DirectedGraph(this)
  def asUndirectedJGraphTGraph = new jgrapht.UndirectedGraph(this)

  /* Until an actual link has been created, the directedness of the links agentset
   * is not defined: i.e., both  linkSet.isDirected and linkSet.isUndirected will 
   * return false. Here, we just check for .isDirected because if no links have been 
   * created, treating the graph as undirected will do no harm. NP 2013-05-15
   */
  def isDirected = linkSet.isDirected

  val world = linkSet.world
  private val linkManager = world.linkManager

  def isValidTurtle(turtle: Turtle) =
    turtle.getBreed eq turtleSet
  def validTurtle(turtle: Turtle): Option[Turtle] =
    if (isValidTurtle(turtle)) Some(turtle) else None

  def isValidLink(link: Link) =
    (link.getBreed eq linkSet) && isValidTurtle(link.end1) && isValidTurtle(link.end2)
  def validLink(link: Link): Option[Link] =
    if (isValidLink(link)) Some(link) else None

  def turtleCount: Int = turtleSet.count
  def linkCount: Int = linkSet.count

  def links: Iterable[Link] = linkSet.asIterable[Link]
  def turtles: Iterable[Turtle] = turtleSet.asIterable[Turtle]

  def allEdges(turtle: Turtle): Iterable[Link] =
    linkManager.findLinksWith(turtle, linkSet).asIterable[Link].filter(isValidLink)
  def allNeighbors(turtle: Turtle): Iterable[Turtle] =
    linkManager.findLinkedWith(turtle, linkSet).asIterable[Turtle].filter(isValidTurtle)

  def directedInEdges(turtle: Turtle): Iterable[Link] =
    linkManager.findLinksTo(turtle, linkSet).asIterable[Link].filter(isValidLink)
  def inNeighbors(turtle: Turtle): Iterable[Turtle] =
    linkManager.findLinkedTo(turtle, linkSet).asIterable[Turtle].filter(isValidTurtle)

  def directedOutEdges(turtle: Turtle): Iterable[Link] =
    linkManager.findLinksFrom(turtle, linkSet).asIterable[Link].filter(isValidLink)
  def outNeighbors(turtle: Turtle): Iterable[Turtle] =
    linkManager.findLinkedFrom(turtle, linkSet).asIterable[Turtle].filter(isValidTurtle)

  // Jung, weirdly, sometimes uses in/outedges with undirected graphs, actually expecting all edges
  def inEdges(turtle: Turtle): Iterable[Link] =
    if (isDirected) directedInEdges(turtle) else allEdges(turtle)
  def outEdges(turtle: Turtle): Iterable[Link] =
    if (isDirected) directedOutEdges(turtle) else allEdges(turtle)
}
