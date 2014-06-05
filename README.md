# head-to-tail

A simple Clojure app for solving the head to tail problem. Be aware there are 
different versions of this problem. This implementation is a more relaxed one,
in terms of changing letters. It uses the popular graph library Loom and gets all
of the words into a graph first. This step takes the longest.

## Usage

### Configuration

```Clojure
{
  :words {
    :head "head"
    :tail "tail"
  }
  :dict {
    :file "data/wordsEn.txt"
  }
};end
```

### Execution

```Clojure
java -jar target/head-to-tail-0.0.2-standalone.jar head-to-tail
Saved adjacency-list is found...
The shortest path:  (head heal heil hail tail)
```


## License

Copyright © 2014 Istvan Szukacs (see LICENSE file)

