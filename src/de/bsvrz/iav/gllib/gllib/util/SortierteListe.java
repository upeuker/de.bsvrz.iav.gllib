package de.bsvrz.iav.gllib.gllib.util;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.RandomAccess;

/**
 * Eine sortierte Liste.
 * <p>
 * TODO: Generic auf "extends Comparable" hinbiegen.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 * @param <E>
 */
public class SortierteListe<E> extends AbstractList<E> implements List<E>,
		Queue<E>, Cloneable, RandomAccess, Serializable {

	private static final long serialVersionUID = 1L;

	/** Interner Buffer der Listenelemente. */
	private transient E[] elementData;

	/** Aktuelle Gr&ouml;&szlig;e der Liste, <em>nicht</em> des Buffers. */
	private int size;

	public static void main(String[] argv) {
		Integer[] list;
		List<Integer> sorted;

		list = new Integer[] { 3, 9, 6, 8, 0, 1, 4, 7, 5, 2 };
		sorted = Arrays.asList(list);
		System.out.println("list: " + sorted);
		sorted = mergesort(sorted);
		System.out.println("sorted: " + sorted + "\n");

		list = new Integer[] { 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };
		sorted = Arrays.asList(list);
		System.out.println("list: " + sorted);
		sorted = mergesort(sorted);
		System.out.println("sorted: " + sorted + "\n");

		list = new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		sorted = Arrays.asList(list);
		System.out.println("list: " + sorted);
		sorted = mergesort(sorted);
		System.out.println("sorted: " + sorted);

		SortierteListe<Integer> liste;
		list = new Integer[] { 3, 9, 6, 8, 0, 1, 4, 7, 5, 2 };
		sorted = Arrays.asList(list);
		List<Integer> l;
		l = new MeinArrayList<Integer>(sorted);
		liste = new SortierteListe<Integer>(sorted);
	}

	/**
	 * Sortiert eine Liste mit Hilfe von Mergesort. Im Gegensatz zur
	 * Sun-Implementation in {@link java.util.Arrays} werden bereits sortierte
	 * Teillisten ber&uuml;cksichtigt.
	 * 
	 * @param <T>
	 *            Typ der zu sortiernden Elemente
	 * @param list
	 *            Die zu sortierende Liste
	 * @return Eine sortierte Kopie der Ausgangsliste
	 */
	public static <T extends Comparable<? super T>> List<T> mergesort(
			List<T> list) {
		List<T> aux;
		List<Integer> runs;

		// Eine Liste mit einem Element ist a priori sortiert
		if (list.size() == 1) {
			return new ArrayList<T>(list);
		}

		// Sortierte Teilfolgen bestimmen
		runs = new ArrayList<Integer>();
		runs.add(0);
		for (int i = 1; i < list.size(); i++) {
			if (list.get(i - 1).compareTo(list.get(i)) > 0) {
				runs.add(i);
			}
		}
		runs.add(list.size());

		// Die Teilfolgen mergen
		aux = new ArrayList<T>();
		for (int i = 0; i < runs.size() - 1; i++) {
			aux = merge(aux, list, runs.get(i), runs.get(i + 1));
		}

		return aux;
	}

	/**
	 * F&uuml;hrt den merge-Schritt des Mergesort aus.
	 * 
	 * @param <T>
	 *            Typ der zu sortierenden Elemente
	 * @param sorted
	 *            Bereits sortierte Teilliste
	 * @param list
	 *            Die komplette zu sortierende Liste
	 * @param start
	 *            Startindex des zu sortierenden Bereichs der Liste
	 * @param end
	 *            Endindex (exklusive) des zu sortierenden Bereichs der Liste
	 * @return Die sortierte Teilleiste bestehend aus den Elementen aus
	 *         {@code sorted} und {@code list}
	 */
	private static <T extends Comparable<? super T>> List<T> merge(
			List<T> sorted, List<T> list, int start, int end) {
		List<T> merge;
		int j, k;

		merge = new ArrayList<T>();
		j = start;
		k = 0;
		while (k < sorted.size() && j < end) {
			if (sorted.get(k).compareTo(list.get(j)) <= 0) {
				merge.add(sorted.get(k++));
			} else {
				merge.add(list.get(j++));
			}
		}
		for (int i = k; i < sorted.size(); i++) {
			merge.add(sorted.get(i));
		}
		for (int i = j; i < end; i++) {
			merge.add(list.get(i));
		}

		return merge;
	}

	/**
	 * Wendet den Mergesort-Algorithmus an, um die Listenelemente zu sortieren.
	 */
	private void mergesort() {
		List<E> sortiert;

		sortiert = new ArrayList<E>(Arrays.asList(elementData));
		while (sortiert.remove(null)) {
			// nix weiter
		}
		sortiert = (List<E>) mergesort((List<Comparable>) sortiert);

		assert elementData.length == sortiert.size();
		for (int i = 0; i < sortiert.size(); i++) {
			elementData[i] = sortiert.get(i);
		}
	}

	/**
	 * Pr&uuml;ft ob der Index nicht gr&ouml;&szlig;er als die Liste ist. Die
	 * Methode wurde aus {@link java.util.ArrayList} &uuml;bernommen.
	 * 
	 * @param index
	 *            Zu pr&uuml;fender Index
	 */
	private void checkIndex(int index) {
		if (index >= size) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: "
					+ size);
		}
	}

	/**
	 * Initialisiert den internen Buffer.
	 * 
	 * @see java.util.ArrayList#ArrayList()
	 */
	public SortierteListe() {
		this(10);
	}

	/**
	 * Initialisiert den internen Buffer.
	 * 
	 * @param initialCapacity
	 *            Anfangsgr&ouml;&szlig; des Buffers
	 * 
	 * @see java.util.ArrayList#ArrayList(int)
	 */
	@SuppressWarnings("unchecked")
	public SortierteListe(int initialCapacity) {
		if (initialCapacity < 0) {
			throw new IllegalArgumentException("Illegal Capacity: "
					+ initialCapacity);
		}

		elementData = (E[]) new Object[initialCapacity];
	}

	/**
	 * Initialisiert den internen Buffer und f&uuml;gt anschlie&szlig;end die
	 * Elemente der {@code Collection} der neuen Liste hinzu.
	 * 
	 * @param c
	 *            Initialelemente der Liste
	 */
	public SortierteListe(Collection<? extends E> c) {
		// Allow 10% room for growth
		this((int) Math.min((c.size() * 110L) / 100, Integer.MAX_VALUE));
		addAll(c);
	}

	/**
	 * Increases the capacity of this <tt>ArrayList</tt> instance, if
	 * necessary, to ensure that it can hold at least the number of elements
	 * specified by the minimum capacity argument.
	 * 
	 * @param minCapacity
	 *            the desired minimum capacity.
	 * @see java.util.ArrayList#ensureCapacity(int)
	 */
	@SuppressWarnings("unchecked")
	public void ensureCapacity(int minCapacity) {
		int oldCapacity = elementData.length;
		if (minCapacity > oldCapacity) {
			Object[] oldData = elementData;
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if (newCapacity < minCapacity) {
				newCapacity = minCapacity;
			}
			elementData = (E[]) new Object[newCapacity];
			System.arraycopy(oldData, 0, elementData, 0, size);
		}
	}

	/**
	 * Returns the first element in this list.
	 * 
	 * @return the first element in this list.
	 * @throws NoSuchElementException
	 *             if this list is empty.
	 */
	public E getFirst() {
		return element();
	}

	/**
	 * Returns the last element in this list.
	 * 
	 * @return the last element in this list.
	 * @throws NoSuchElementException
	 *             if this list is empty.
	 */
	public E getLast() {
		if (size == 0) {
			throw new NoSuchElementException();
		}

		return elementData[size - 1];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(E o) {
		ensureCapacity(size + 1);
		elementData[size++] = o;
		mergesort();
		return true;
	}

	/**
	 * Gibt das Element mit einem bestimmten Index zur&uuml;ck.
	 * 
	 * @param index
	 *            Index des Elements
	 * @return Das Element
	 */
	@Override
	public E get(int index) {
		checkIndex(index);
		return elementData[index];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public E remove(int index) {
		checkIndex(index);

		modCount++;
		E oldValue = elementData[index];

		int numMoved = size - index - 1;
		if (numMoved > 0) {
			System.arraycopy(elementData, index + 1, elementData, index,
					numMoved);
		}
		elementData[--size] = null;

		return oldValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * {@inheritDoc}
	 */
	public E element() {
		E x = peek();
		if (x != null) {
			return x;
		}

		throw new NoSuchElementException();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean offer(E o) {
		return add(o);
	}

	/**
	 * {@inheritDoc}
	 */
	public E peek() {
		if (size == 0) {
			return null;
		}
		return elementData[0];
	}

	/**
	 * {@inheritDoc}
	 */
	public E poll() {
		return remove(0);
	}

	/**
	 * {@inheritDoc}
	 */
	public E remove() {
		E x = poll();
		if (x != null) {
			return x;
		}

		throw new NoSuchElementException();
	}

}
