public class MergeSort {
	private static int cutoffLevel;
	private final static int DEFAULT_CUTOFF = 4;
	private static Utility helper = new Utility();

	public static void main(String[] args) {
		helper.checkArgs(args);
		
		cutoffLevel = args.length == 3 ? Integer.parseInt(args[2]) : DEFAULT_CUTOFF;
		boolean parallel = args.length !=1 ? true : false;;
		int numElements = Integer.parseInt(args[0]);

		int list[] = helper.createList(numElements);		long startTime = System.nanoTime();
		
		MergeSort sorter = new MergeSort();

		if (parallel)
			sorter.parallelMergeSort(list, numElements, 0);
		else
			sorter.mergeSort(list, numElements);

		if (!helper.isSorted(list))
			System.out.println("Merge Sort did not successfully sort list!");
		else
			System.out.println("Merge Sort with " + (parallel ? "" : "out ") + "threads\n" + "\tNum elements:  "
					+ numElements + "\n\tRuntime: " + helper.calculateRunTime(startTime) + " seconds");

	}

	// Splits list[] into two subarrays(left and right side) to be sorted by merge function.
	//Left and Right side are recursively split 
	public void mergeSort(int[] list, int n) {
		if (n < 2)
			return;

		// split list into left and right by using the middle index of the list
		int midIndex = n / 2;
		int[] left = new int[midIndex];
		int[] right = new int[n - midIndex];

		for (int i = 0; i < midIndex; i++)
			left[i] = list[i];

		for (int i = midIndex; i < n; i++)
			right[i - midIndex] = list[i];

		mergeSort(left, midIndex);
		mergeSort(right, n - midIndex);

		//merge left and right side back together
		merge(list, left, right, midIndex, n - midIndex);

	}

	// Splits list[] into two subarrays(left and right side) to be sorted by merge function.
	//Left and Right side are recursively split and done in parallel by the use of threads
	public void parallelMergeSort(int[] list, int n, int level) {
		// call regular mergeSort if cutoff level is reached
		if (level >= cutoffLevel) {
			mergeSort(list, n);
			return;
		}
		if (n < 2)
			return;

		// split list into left and right by using the middle index of the list
		int midIndex = n / 2;
		int[] left = new int[midIndex];
		int[] right = new int[n - midIndex];

		for (int i = 0; i < midIndex; i++)
			left[i] = list[i];

		for (int i = midIndex; i < n; i++)
			right[i - midIndex] = list[i];

		// creates thread that will call parallel merge on left list (this does not start thread)
		Thread leftThread = new Thread() {
			public void run() {
				parallelMergeSort(left, midIndex, level + 1);
			}
		};

		// creates thread that will call parallel merge on right list (this does not start thread)
		Thread rightThread = new Thread() {
			public void run() {
				parallelMergeSort(right, n - midIndex, level + 1);
			}
		};

		// starts both threads that will execute in parallel
		leftThread.start();
		rightThread.start();
		try {
			// calling thread waits until leftThread terminates
			leftThread.join();
			// calling thread waits until rightThread terminates
			rightThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// results from leftThread and rightThread are used in the regular merge method
		merge(list, left, right, midIndex, n - midIndex);

	}

	// Merges two subarrays from list[] in sorted order
	private void merge(int[] list, int[] left, int[] right, int leftSize, int rightSize) {
		int l = 0, r = 0, i = 0;

		while (l < leftSize && r < rightSize) {
			if (left[l] <= right[r])
				list[i++] = left[l++];
			else
				list[i++] = right[r++];
		}
		
		while (l < leftSize)
			list[i++] = left[l++];

		while (r < rightSize)
			list[i++] = right[r++];

	}

}
