# NetLogo-Extension-DBSCAN
NetLogo extension for DBSCAN clustering algorithm

It allows you to perform unsupervised density-based clustering based on specified turtle variables or proximity. The main advantage over supervised algorithms such as K-Means is that it is not necessary to specify the number of resulting clusters in advance.

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

Example (NetLogo 5):

```
; Colour and label the agents by cluster
let ctr 1
(foreach clusters (n-of (length clusters) base-colors)
  [ let aset turtles with [ member? self ?1 ]
    ask aset
      [ set color ?2
        set label (word "ID: " who ", Cluster: " ctr ", Wealth: " wealth) ]
    ; Print agent cluster sets
    output-print (word "Cluster " ctr ": " aset)
    set ctr (ctr + 1) ])
```

The same example in NetLogo 6 or higher (due to syntax changes in NetLogo 6):

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
patches-own [ resource ]

... and populate variable ...

; Cluster patches by variable "resource", with at least 3 members to constitute a cluster, and a maximum value difference of 20
let clusters dbscan:cluster-by-variable patches "resource" 3 20
```

The output can then be modified as exemplified in the following (only for NetLogo 6 onwards):

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
The clusters can then be used as shown in the section **Clustering individuals by variable**.

## Demo

For more comprehensive examples for the feature set, try out the demo that corresponds to your NetLogo version.

* For NetLogo 5, use [demo/dbscan-clustering-demo-v5.nlogo](https://github.com/chrfrantz/NetLogo-Extension-DBSCAN/blob/master/demo/dbscan-clustering-demo-v5.nlogo). (Note: the NetLogo 5 demo model does not include an example for clustering patches; this feature is only available for NetLogo 6 onwards.)

* For NetLogo 6 (and higher), use [demo/dbscan-clustering-demo-v6.nlogo](https://github.com/chrfrantz/NetLogo-Extension-DBSCAN/blob/master/demo/dbscan-clustering-demo-v6.nlogo).

Clustering agents by location using any of the demo models should produce the following output (in this example the cluster centroid is shown as a coloured patch).

![Location-based clustering demo output](https://github.com/chrfrantz/NetLogo-Extension-DBSCAN/raw/master/doc/ExampleLocationBasedClusteringOutput.png)

For a screenshot of the entire demo GUI including control elements and further cluster-related information have a look [here](https://github.com/chrfrantz/NetLogo-Extension-DBSCAN/raw/master/doc/NetLogoDbscanDemoGui.png).

## Deployment

The preferred deployment variant is simply to use the auto-installation feature of NetLogo 6.1 (see Variant 0). Alternatively, you can install it using the Extension Manager provided by NetLogo 6.1 and higher (see Variant 1). For older versions of NetLogo (i.e., 6.0 or lower) you can download the corresponding jar files directly (Variant 2). Alternatively, for all supported versions of NetLogo you can build the extension from source (Variant 3).

### Variant 0 (requires NetLogo 6.1.0 or higher): Auto-Installation

To trigger the installation, simply add `extensions [dbscan]` at the top of your model code and click on the `Check` button in the editor. NetLogo will then prompt you to confirm the installation of the extension and you can immediately use it in your code. You can manage the installed extension in the Extension Manager. For more details about the Extension Manager, see its official [documentation](http://ccl.northwestern.edu/netlogo/docs/extension-manager.html).

### Variant 1 (requires NetLogo 6.1.0 or higher): Manual Installation via NetLogo Extension Manager

Use the Extension Manager provided by NetLogo by selecting `Tools` in the menu bar, followed by `Extensions ...`. In the dialog, scroll down to the extension `DBSCAN` and click `Install`. You can now use it in your models. For more details about the Extension Manager, see its official [documentation](http://ccl.northwestern.edu/netlogo/docs/extension-manager.html).

### Variant 2: Downloading jar files and placing in extensions folder

To install the extension, download the zip file containing the latest version from the [releases page](https://github.com/chrfrantz/NetLogo-Extension-DBSCAN/releases) for your NetLogo version (5, 6, or 6.1). It contains a folder `dbscan` that contains all relevant jar files. Unzip it to the `extensions` folder of your NetLogo installation (the final structure should correspond to `NetLogo x.x.x/app/extensions/dbscan/<jar files>`, with `x.x.x` being the installed NetLogo version).

### Variant 3: Building from source

You can build the extension from scratch using maven after cloning the repository. 

* For NetLogo version 5, run `mvn clean package -f pom-v5.xml`

* For NetLogo version 6, run `mvn clean package -f pom-v6.xml`

* For NetLogo version 6.1, run `mvn clean package -f pom-v6.1.xml`

In addition, you will need to build the [DBSCAN repository](https://github.com/chrfrantz/DBSCAN.git) (Command: `mvn package`) which contains the underlying DBSCAN algorithm. Place both jar files in the extensions subfolder `dbscan` (following the structure described under Variant 2).

## Bugs and requests

If you discover bugs or have requests regarding additional features, please create an issue in the [issue tracker](https://github.com/chrfrantz/NetLogo-Extension-DBSCAN/issues).


