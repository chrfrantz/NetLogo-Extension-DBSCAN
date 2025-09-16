# NetLogo-Extension-DBSCAN
NetLogo extension for DBSCAN clustering algorithm

The extension allows you to perform unsupervised density-based clustering of turtles/agents and patches based on specified variables or by proximity. The main advantage over supervised algorithms such as K-Means is that it is not necessary to specify the number of resulting clusters in advance.

This extension supports all versions of [NetLogo](https://ccl.northwestern.edu/netlogo/) from 5 onwards. This version (0.5) is focused on NetLogo 7. 
Versions of this extension (all versions up to 0.4) for NetLogo versions 5 up to version 6.4 are provided in the repository [branch v6.1](https://github.com/chrfrantz/NetLogo-Extension-DBSCAN/tree/v6.1) (including code examples). This Readme covers aspects specific to the latest extension version. 

The feature set of the extension varies depending on the NetLogo version, with full feature support since NetLogo 6 onwards (see details below).

Author: Christopher Frantz (cf at christopherfrantz dot org)

## Usage

The extension contains two reporters, `cluster-by-variable` and `cluster-by-location` that support the clustering of individuals and patches.

### Clustering individuals by variable

(since version 0.1)

Syntax: `cluster-by-variable` **agents-to-be-clustered** **cluster-variable** **minimum-members** **maximum-distance**

Clusters a given agentset **agents-to-be-clustered** by individual-level variable **cluster-variable**, along with two hyperparameters required for the operation of DBSCAN: 
* **minimum-members**: a minimum number of agents to constitute a cluster, and 
* **maximum-distance**: the maximum cluster variable value difference within a cluster.

The reporter returns a nested list of clustered agents.

Example:

```
; Import extension
extensions [dbscan]

... instantiate agents with variable "wealth" ...

; Cluster agents by variable "wealth", with at least 3 members to constitute a cluster, and a maximum value difference of 3
let clusters dbscan:cluster-by-variable agents "wealth" 3 3
```

You can then use the cluster information to modify the output, e.g. by colouring and labelling the turtles.

Example (NetLogo 6 and 7):

```
; Colour and label the agents by cluster
let ctr 1
(foreach clusters (n-of (length clusters) base-colors)
  [ [ x y ] -> let aset turtles with [ member? self x ]
    ask aset
      [ set color y
        set label (word "ID: " who ", Cluster: " ctr ", Wealth: " wealth) ]
    ; Print agent cluster sets
    output-print (word "Cluster " ctr ": " aset)
    set ctr (ctr + 1) ])
```

### Clustering patches by variable

(since version 0.3)

**Since version 0.3**, this extension also **supports the clustering of patches by variable**. However, this feature is **only supported for NetLogo 6 or higher**. Just to clarify, you can still install the latest version of the extension in NetLogo 5, but you won't be able to cluster patches.

Syntax: `cluster-by-variable` **patches-to-be-clustered** **cluster-variable** **minimum-members** **maximum-distance**

Clusters a given set of patches **patches-to-be-clustered** by patch variable **cluster-variable**, along with two hyperparameters required for the operation of DBSCAN: 
* **minimum-members**: a minimum number of patches to constitute a cluster, and 
* **maximum-distance**: the maximum cluster variable value difference within a cluster.

The reporter returns a nested list of clustered patches.

Example:

```
; Import extension
extensions [dbscan]

... instantiate patches with variable "resource" ...
patches-own [resource]

... and populate variable ...

; Cluster patches by variable "resource", with at least 3 members to constitute a cluster, and a maximum value difference of 20
let clusters dbscan:cluster-by-variable patches "resource" 3 20
```

The output can then be processed as exemplified in the following (for NetLogo 6 and 7):

```
; Colour and label the agents by cluster
let ctr 1
(foreach clusters (n-of (length clusters) base-colors)
  [ [ x y ] -> let aset patches with [ member? self x ]
    ask aset
      [ set pcolor y ]
    ; Print patch cluster sets
    output-print (word "Cluster " ctr ": " aset)
    set ctr (ctr + 1) ])
```

### Clustering individuals by location

(since version 0.1)

Syntax: `cluster-by-location` **agents-to-be-clustered** **minimum-members** **maximum-distance**

Clusters a given agentset **agents-to-be-clustered** by proximity, along with two hyperparameters required for the operation of DBSCAN: 
* **minimum-members**: a minimum number of agents to constitute a cluster, and
* **maximum-distance**: the maximum permissible distance between agents within a cluster.

The reporter returns a nested list of clustered agents.

Example:

```
; Import extension
extensions [dbscan]

... instantiate agents with coordinates ...

; Cluster agents by location, with at least 3 members to constitute a cluster, and a maximum distance of 3
let clusters dbscan:cluster-by-location agents 3 3
```
The clusters can then be used as shown in the section [**Clustering individuals by variable**](https://github.com/chrfrantz/NetLogo-Extension-DBSCAN#clustering-individuals-by-variable).

## Demo

For more comprehensive examples for the feature set, try out the demo provided as part of the extension under [demo/dbscan-clustering-demo-v7.nlogox](https://github.com/chrfrantz/NetLogo-Extension-DBSCAN/blob/master/demo/dbscan-clustering-demo-v7.nlogox). 

Demo models for NetLogo versions 5 and 6 are provided alongside versions 0.4 of the extension (see repository [branch v6.1](https://github.com/chrfrantz/NetLogo-Extension-DBSCAN/tree/v6.1)).

Clustering agents by location should produce the following output (in this example the cluster centroid is shown as a coloured patch).

![Location-based clustering demo output](https://github.com/chrfrantz/NetLogo-Extension-DBSCAN/raw/master/doc/ExampleLocationBasedClusteringOutput.png)

For a screenshot of the entire demo GUI including control elements and further cluster-related information have a look [here](https://github.com/chrfrantz/NetLogo-Extension-DBSCAN/raw/master/doc/NetLogo7DbscanDemoGuiAgentLocation.png).

## Deployment

*NetLogo supports various deployment approaches, ranging from simple auto-installation to building the extension from source. This section describes all the options, but note that this branch only contains files for extension version 0.5 (for NetLogo 7); code for older NetLogo versions (5 and 6) can be found in the repository branch v6.1.*

Since NetLogo 6.1 the preferred deployment variant is the use of the auto-installation feature (see Variant 0). Alternatively, you can install the extension using the Extension Manager provided from NetLogo 6.1 onwards (see Variant 1). For older versions of NetLogo (i.e., 6.0 or lower) you can download the corresponding jar files directly (Variant 2). Alternatively, for all supported versions of NetLogo you can build the extension from source (Variant 3).

### Variant 0 (requires NetLogo 6.1.0 or higher): Auto-Installation

To trigger the installation, simply add `extensions [dbscan]` at the top of your model code and click on the `Check` button in the editor (or switch tabs). NetLogo will then prompt you to confirm the installation of the extension (NetLogo 6.1.0 onward) or indicate that the extension is not present and open the Extension Manager for you to install the extension manually (NetLogo 7.0.0), and once installed, you can immediately use the clustering features in your code (Note: Sometimes a restart of NetLogo seems necessary to activate newly installed extensions). You can manage the installed extension in the Extension Manager. For more details about the installation via Extension Manager, see Variant 1.

You can also just download and open the [NetLogo 7 DBSCAN demo model](https://github.com/chrfrantz/NetLogo-Extension-DBSCAN/blob/master/demo/dbscan-clustering-demo-v7.nlogox) to trigger the installation.

### Variant 1 (requires NetLogo 6.1.0 or higher): Manual Installation via NetLogo Extension Manager

Use the Extension Manager provided by NetLogo by selecting `Tools` in the menu bar, followed by `Extensions ...`. In the dialog, scroll down to the extension `DBSCAN` and click `Install`. You can now use it in your models. For further details about the Extension Manager, see its official [documentation](http://ccl.northwestern.edu/netlogo/docs/extension-manager.html).

### Variant 2: Downloading jar files and placing in extensions folder

To install the extension, download the zip file containing the latest version from the [releases page](https://github.com/chrfrantz/NetLogo-Extension-DBSCAN/releases) for your NetLogo version (extension version 0.4 for NetLogo 5 and 6; extension version 0.5 for NetLogo 7). It contains a folder `dbscan` that contains all relevant jar files. Unzip it to the `extensions` folder of your NetLogo installation (the final structure should correspond to `NetLogo x.x.x/app/extensions/dbscan/<jar files>`, with `x.x.x` being the installed NetLogo version).

### Variant 3: Building from source

You can build the extension for NetLogo 7 from scratch using [maven](https://maven.apache.org/) after cloning the repository. 

* Simply run `mvn clean package` in the repository directory.

* For older NetLogo versions, please consult the [Readme in branch v6.1](https://github.com/chrfrantz/NetLogo-Extension-DBSCAN/tree/v6.1#readme) (it also covers installation aspects related to Netlogo version 5).

In addition, you will need to build the [DBSCAN repository](https://github.com/chrfrantz/DBSCAN.git) (Command: `mvn clean package`) which contains the underlying DBSCAN algorithm. Place both jar files in the extensions subfolder `dbscan` (following the structure described under Variant 2). Note that all builds have been primarily tested using Java 11.

## Bugs and requests

If you discover bugs or have requests regarding additional features, please create an issue in the [issue tracker](https://github.com/chrfrantz/NetLogo-Extension-DBSCAN/issues).


