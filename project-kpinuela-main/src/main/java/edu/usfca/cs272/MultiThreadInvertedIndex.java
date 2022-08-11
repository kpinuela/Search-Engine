package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Set;

/**
 * @author kyle MultiThreaded Inverted Index
 *
 */
public class MultiThreadInvertedIndex extends InvertedIndex {
	/**
	 * initialize read write lock
	 */
	private final SimpleReadWriteLock lock;

	/**
	 * Initializes an thread safe Inverted Index
	 */
	public MultiThreadInvertedIndex() {
		super();
		this.lock = new SimpleReadWriteLock();
	}

	/**
	 * Returns the identity hashcode of the lock object. Not particularly useful.
	 *
	 * @return the identity hashcode of the lock object
	 */
	public int lockCode() {
		return System.identityHashCode(lock);
	}

	@Override
	public void indexWrite(Path path) throws IOException {
		lock.readLock().lock();
		try {
			super.indexWrite(path);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void countWrite(Path path) throws IOException {
		lock.readLock().lock();
		try {
			super.countWrite(path);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void add(String location, String path, int value) throws IOException {
		lock.writeLock().lock();
		try {
			super.add(location, path, value);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public int getWordCount(String path) {
		lock.readLock().lock();
		try {
			return super.getWordCount(path);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public ArrayList<Result> exactSearch(Set<String> queryStems) {
		lock.readLock().lock();
		try {
			return super.exactSearch(queryStems);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public ArrayList<Result> partialSearch(Set<String> queryStems) {
		lock.readLock().lock();
		try {
			return super.partialSearch(queryStems);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int size() {
		lock.readLock().lock();
		try {
			return super.size();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int size(String word) {
		lock.readLock().lock();
		try {
			return super.size(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int size(String word, String location) {

		lock.readLock().lock();
		try {
			return super.size(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean contains(String word) {
		lock.readLock().lock();
		try {
			return super.contains(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean contains(String word, String location) {
		lock.readLock().lock();
		try {
			return super.contains(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean contains(String word, String location, int position) {
		lock.readLock().lock();
		try {
			return super.contains(word, location, position);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public String toString() {
		lock.readLock().lock();
		try {
			return super.toString();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> get() {
		lock.readLock().lock();
		try {
			return super.get();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> get(String word) {
		lock.readLock().lock();
		try {
			return super.get(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> get(String word, String location) {
		lock.readLock().lock();
		try {
			return super.get(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void addAll(InvertedIndex local) {
		lock.writeLock().lock();
		try {
			super.addAll(local);
		} finally {
			lock.writeLock().unlock();
		}
	}

}