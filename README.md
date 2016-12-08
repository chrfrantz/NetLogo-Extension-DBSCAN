# NetLogo-Extension-DBSCAN
NetLogo extension for DBSCAN clustering algorithm

It allows you to perform unsupervised density-based clustering based on specified turtle variables or proximity. The main advantage over supervised algorithms such as K-Means is that it is not necessary to specify the number of resulting clusters in advance.

## Usage

The extension contains two reporters, `cluster-by-variable` and `cluster-by-location`.

### Clustering by variable

Syntax: `cluster-by-variable` *agents-to-be-clustered* *cluster-variable* *minimum-members* *maximum-distance*

Clusters a given agentset *agents-to-be-clustered* by variable *cluster-variable*, requiring a minimum of *minimum-members* agents to constitute a cluster within a range of variable value difference, with the maximum value difference being *maximum-distance*.

The reporter returns a nested list of clustered agents.

Example:

```

... instantiate agents with variable "wealth" ...

; Cluster agents by variable "wealth", with at least 3 members to constitute a cluster, and a maximum value difference of 3
let clusters dbscan:cluster-by-variable agents "wealth" 3 3

; Colour and label the agents by cluster
let ctr 1
(foreach clusters (n-of (length clusters) base-colors)
  [ let aset turtles with [member? self ?1 ]
    ask aset
      [ set color ?2
        set label (word "ID: " who ", Cluster: " ctr ", Wealth: " wealth) ]
    ; Print agent sets
    output-print (word "Cluster " ctr ": " aset)
    set ctr (ctr + 1) ])
```

### Clustering by location

`cluster-by-location` *agentset-to-be-clustered* *minimum-members* *maximum-distance*

Clusters a given agentset *agents-to-be-clustered* by proximity, requiring a minimum of *minimum-members* agents to constitute a cluster with a maximum distance *maximum-distance*.

The reporter returns a nested list of clustered agents.

Example:

... instantiate agents with coordinates ...

; Cluster agents by location, with at least 3 members to constitute a cluster, and a maximum distance of 3
let clusters dbscan:cluster-by-location agents 3 3

; Colour and label individual agents by cluster
let ctr 1
(foreach clusters (n-of (length clusters) base-colors)
  [ let aset turtles with [member? self ?1 ]
    ask aset
      [ set color ?2
        set label (word "ID: " who ", Cluster: " ctr) ]
    ; Print agent sets
    output-print (word "Cluster " ctr ": " aset)
    set ctr (ctr + 1) ])
```

## Demo

For more comprehensive examples for both reporters, try out the demo under `demo/dbscan-clustering-demo.nlogo`

Clustering agents by location should produce the following output.

![Location-based clustering demo output](https://github.com/chrfrantz/NetLogo-Extension-DBSCAN/raw/master/doc/ExampleLocationBasedClusteringOutput.png)

## Deployment

### Variant 1: Downloading jar files

To install the extension, download the zip file containing the latest version from the releases page and unzip it in the `NetLogo/extensions` folder of your NetLogo installation (the final structure should be `NetLogo/extensions/dbscan/<jar files>`).

### Variant 2: Building from source

You can build the extension from scratch using maven by running `mvn package` after cloning the repository. In addition, you will need to do the same for the [DBSCAN repository](https://github.com/chrfrantz/DBSCAN.git) which contains the underlying DBSCAN algorithm. Place both jar files in the extensions subfolder `dbscan` (see Variant 1).


