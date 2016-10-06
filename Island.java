import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Vector;

public class Island {
	private static int[][] array;	// искомая матрица с разным уровнем высоты
	// матрица состояний. 
	private static int[][] flagArray;	
	//Виды состояний: 
	//white_state = 1 - начальное состояние высоты или уровень воды еще не поднялся до конца, высота не обрабатывается
	//black_state = 0 - граничный элемент или конечное состояние высоты, т.е. уровень воды поднялся максимально
	// gray_state = 2 - состояние, в котором уровень воды поднимается, высота обрабатывается(т.е. наливается вода)
	private static int globalSum = 0; // общий объём воды, скапливающейся на острове после дождей
	private static int stateSum = 0;  // количество состояний white_state  в матрице flagArray
	private static int minInWay = 1001; // наименьшая высота из элементов, кот. граничат с элементами одной высоты
	private static int rowSize, colSize, // размеры матрицы
					   gxIn, gyIn;  // глобальный минимум матрицы
	// flagArray - элементы первых и последних строк и столбцов принимают black_state, 
	// все остальные элементы - white_state
	private static void init(){
		int localMin = 1001;
		for (int row = 1; row < rowSize - 1; row++) {
			for (int col = 1; col < colSize - 1; col++) {
				if (array[row][col] < localMin ){
					localMin = array[row][col];
					gxIn = row; gyIn = col;
				}
				flagArray[row][col] = 1; // white state
			}
		}
		for (int i = 0; i < rowSize - 1; i++) {
			flagArray[i][0] = 0;
			flagArray[i][colSize - 1] = 0;  // black state
		}
		for (int i = 0; i < colSize - 1; i++) {
			flagArray[0][i] = 0;
			flagArray[rowSize - 1][i] = 0;
		}
		stateSum = (rowSize - 2) * (colSize - 2);
	}
	private static void getLocalMinInArray(){
		int localMin = 1001;
		for (int row = 1; row < rowSize - 1; row++) {
			for (int col = 1; col < colSize - 1; col++) {
				if (array[row][col] < localMin && flagArray[row][col] == 1){
					localMin = array[row][col];
					gxIn = row; gyIn = col;
				}
			}
		}		// Пока вода заполняет остров - есть хотя бы одно white_state
		while(stateSum > 0){
			while(updateArray(gxIn, gyIn))
				minInWay = 1001;
			getLocalMinInArray();
		}
	}
	// Логика обхода матрицы в глубину
	private static boolean updateArray(int row, int col){
		int minMax = 0;	// Минимальная высота среди соседей элемента с координатами (row,col)
		int[] minSort = new int[4];
		boolean	bResult = false, 
				bCheckInclude = false;
		boolean up = (row - 1 == 0) ? true : false, 
				down = (row + 1 == rowSize - 1) ? true : false,
				right = (col + 1 == colSize - 1) ? true : false,
				left = (col - 1 == 0) ? true : false;
		int[] dx = {1, -1, 0, 0};
		int[] dy = {0, 0, 1, -1};
		int newRow, newCol;
		boolean[] bDownUpRightLeft = {false, false, false, false};
		if (flagArray[row][col] == 0){  // Обнуление флага
			stateSum -= 1;
			return false;					
		}
		minSort = minSort4(array[row][col-1], array[row][col+1],
				 		   array[row-1][col], array[row+1][col]);
		minMax = minSort[0];  // минимальный из соседей
		if (minMax > array[row][col]) {
			globalSum += minMax - array[row][col];
			array[row][col] = minMax;
			return updateArray(row, col);
		}
		else if (minMax == array[row][col]){
			// Проверка на слив воды через границу острова
			if (up    && (array[row][col] == array[0][col]) || 
				down  && (array[row][col] == array[rowSize-1][col]) || 
				right && (array[row][col] == array[row][colSize-1]) ||
				left  && (array[row][col] == array[row][0]))
			{
				flagArray[row][col] = 0;
				stateSum -= 1;
				return false;	
			}
			flagArray[row][col] = 2;// gray state
			for (int i = 1; i < minSort.length; i++) 
				if (minSort[i] < minInWay && minSort[i] != minMax){
					minInWay = minSort[i];
					break;
				}
			// dfs.1
			for(int i = 0; i < 4; i++){ 
				newRow = row + dx[i];
				newCol = col + dy[i];
				if (minMax == array[newRow][newCol] && flagArray[newRow][newCol] != 2) {
					bDownUpRightLeft[i] = updateArray(newRow, newCol);
					bCheckInclude = true;
				}
			}
			
			if (bCheckInclude){
				
				for(int i = 0; i < 4; i++){
					bResult = bResult || bDownUpRightLeft[i];
				}
				
				if (bResult) {
					globalSum += minInWay - array[row][col];
					array[row][col] = minInWay;
					flagArray[row][col] = 1;
					return true;
				}
				flagArray[row][col] = 0;
				return false;
			}
			else {
				globalSum += minInWay - array[row][col];
				array[row][col] = minInWay;
				flagArray[row][col] = 1;
				return true;
			}			
		}			
		else { //minMax < array[row][col]

			if (up || down || right || left){
				flagArray[row][col] = 0;
				stateSum -= 1;
				return false;				
			}
			// dfs.2
			for(int i = 0; i < 4; i++){ 
				newRow = row + dx[i];
				newCol = col + dy[i];
				if (minMax == array[newRow][newCol]) {
					bDownUpRightLeft[i] = updateArray(newRow, newCol);
				}
			}
			for(int i = 0; i < 4; i++){
				bResult = bResult || bDownUpRightLeft[i];
			}
			return bResult;
		}
	}

	private static int[] minSort4(int... numbers) {
        if (numbers.length == 0)
            throw new IllegalArgumentException("You forget to write down numbers!");
        for (int i = 0; i < numbers.length; i++)
            for (int j = i + 1; j < numbers.length; j++)
                if (numbers[i] > numbers[j])
                {
                    int tmp = numbers[i];
                    numbers[i] = numbers[j];
                    numbers[j] = tmp;
                }
        return numbers;
    }	

	public static void main(String[] args) throws IOException {
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String s;
		String[] sSize;
		Vector<int[][]> v = new Vector<int[][]>();
		int[][] sizeArray;
		int count, row_, column;
		try {  
			s = in.readLine();
			count = Integer.valueOf(s);
			sizeArray = new int[count][2];
			for (int i = 0; i < count; i++) {
				sSize = in.readLine().split(" ");
				row_ = Integer.valueOf(sSize[0]);
				column = Integer.valueOf(sSize[1]);
				array = new int[row_][column];
				sizeArray[i][0] = row_;
				sizeArray[i][1] = column;
				for (int row = 0; row < row_; row++) {
					sSize = in.readLine().split(" ");
					for (int col = 0; col < column; col++) {
						array[row][col] = Integer.valueOf(sSize[col]);
					}
				}
				v.add(array);
			}
			int i = 0;
			for (Iterator<int[][]> iterator = v.iterator(); iterator.hasNext();) {
				array = iterator.next();
				rowSize = sizeArray[i][0];
				colSize = sizeArray[i][1];
				flagArray = new int[rowSize][colSize];
				init();
				globalSum = 0;
				getLocalMinInArray();
				System.out.println(globalSum);		
				i++;
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}