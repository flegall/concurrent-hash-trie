# About 
An attempt to implement a concurrent hash trie in java, mostly done in order to learn a bit more low-level concurrent data structures.

Idea + implementation techniques found in these reports written by Aleksandar Prokopec: 
- http://infoscience.epfl.ch/record/166908/files/ctries-techreport.pdf
- http://lamp.epfl.ch/~prokopec/ctries-snapshot.pdf

Implementation status : 
- The given implementation is complete (implements all the ConcurrentMap & Iterator methods), and passes all the tests I've written.
- Iteration and bulk operations (.putAll (), EntrySet.removeAll (), EntrySet.retainAll (), toArray ()) are weakly consistent as in ConcurrentHashMap & ConcurrentSkipListMap.
- It doesn't perform that well, seems like 2 to 6 times slower than synchronized maps or ConcurrentHashMap implementations (depending on the operations). 
- It's a bit faster than a synchronized TreeMap or or ConcurrentSkipListMap : around 30% to 80% faster)

# License
Licensed under the ZLib License :
http://en.wikipedia.org/wiki/Zlib_License

			Copyright (c) 2011 Florent Le Gall
			
			This software is provided 'as-is', without any express or implied
			warranty. In no event will the authors be held liable for any damages
			arising from the use of this software.
			
			Permission is granted to anyone to use this software for any purpose,
			including commercial applications, and to alter it and redistribute it
			freely, subject to the following restrictions:
			
			   1. The origin of this software must not be misrepresented; you must not
			   claim that you wrote the original software. If you use this software
			   in a product, an acknowledgment in the product documentation would be
			   appreciated but is not required.
			
			   2. Altered source versions must be plainly marked as such, and must not be
			   misrepresented as being the original software.
			
			   3. This notice may not be removed or altered from any source
			   distribution.
