# ShareBox

Sharebox is a BitTorrent style file sharing application.

## About

### Features:
* We have identified the following features for our file sharing application :
  * BitTorrent protocol
    * Metainfo :
      * Torrent file generator/updater
      * Torrent file parser
      * Encoder to encode the information into bencoding format
      * Decoder to decode the bencoding format
    * Centralized Discovery/tracker server
    
  * BitTorrent client application
    * Connection establishment & handshakes
    * Connection state
    * Reading/Writing pieces from/into files
    * Sending/Receiving data pieces
    * Handling requests - Sending/Responding to requests
    * Handling choke/unchoke messages
    * Handling n connections simultaneously
    * Implement 'Tit-for-Tat' incentive algorithm
    * Implement 'rarest first' algorithm to prioritize rare peices
    * Use Hashing to check data integrity
  * Develop a terminal UI to browse torrents
  * Develop a good suite of test cases
  
### Optional Features:
* We have identified the following optional features which are good to have in the project :

    * Decentralized tracker
    * Explore latest technologies to retreive torrent files such as IPFS
    * Explore if there is a modern way to get peer information.
    * Add more features to UI including download speed, number of live seeders/leechers etc.

## Developers:
 * Alberto : [adelgadocabrera](https://github.com/adelgadocabrera)
 * Anchit : [anchitbhatia](https://github.com/anchitbhatia)
    
