import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GobangGame {
    // 定义达到赢的条件的棋子数目
    private final int WIN_COUNT = 5;
    // 定义用户输入的坐标X
    private int posX = 0;
    // 定义用户输入的Y坐标
    private int posY = 0;

    // 定义棋盘
    private Chessboard chessboard;

    /**
     * 空的构造器
     */
    public GobangGame(){}

    /**
     * 构造器，初始化棋盘和棋子属性
     *
     * @param chessboard
     *          棋盘类
     */
    public GobangGame (Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    /**
     * 检查输入是否合法
     *
     * @param inputStr
     *          有控制台输入的字符串。
     * @return
     *          字符串合法返回true，反则返回false。
     */

    public boolean isValid(String inputStr) {
       // 将用户输入的字符串以逗号隔开，分成两个字符串
       String[] posStrArr = inputStr.split(",");
       try {
           posX = Integer.parseInt(posStrArr[0]) - 1;
           posY = Integer.parseInt(posStrArr[1]) - 1;
       } catch (NumberFormatException e) {
           chessboard.printBoard();
           System.out.println("请以(数字, 数字)的格式输入：");
           return false;
       }
       // 检查输入值是否在范围内
        if (posX < 0 || posX >= Chessboard.BOARD_SIZE || posY < 0
                || posY >= Chessboard.BOARD_SIZE) {
           chessboard.printBoard();
           System.out.println("X与Y坐标只能大于等于1，与小于等于" + Chessboard.BOARD_SIZE
                + "请重新输入：");
           return false;
        }
        // 检查是否已经有棋子
        String[][] board = chessboard.getBoard();
       if (board[posX][posY] != "十") {
           chessboard.printBoard();
           System.out.println("此位置已经有棋子了，请重新输入：");
           return false;
       }

       return true;
    }

    /**
     * 开始下棋
     */
    public void start() throws Exception {
        // true游戏结束
        boolean isOver = false;
        chessboard.initBoard();
        chessboard.printBoard();
        // 获取键盘输入
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String inputStr = null;
        // br.readLine:每当键盘输入一行内容按回车键，则输入的内容被br读取到
        while ((inputStr = br.readLine()) != null) {
            isOver = false;
            if (!isValid(inputStr)) {
                // 如果不合法，要求重新输入，在继续
                continue;
            }
            // 把对应的数组元素赋值为“●”
            String chessman = Chessman.BLACK.getChessman();
            chessboard.setBoard(posX, posY, chessman);
            // 判断用户是否赢了
            if (isWon(posX, posY, chessman)) {
                isOver = true;
            } else {
                // 计算机随机选择坐标位置
                int[] computerPosAr = computerDo();
                chessman = Chessman.WHITE.getChessman();
                chessboard.setBoard(computerPosAr[0], computerPosAr[1], chessman);
                // 判断计算机是否赢了
                if (isWon(computerPosAr[0], computerPosAr[1], chessman)) {
                    isOver = true;
                }
            }
            // 如果产生胜者，询问用户是否继续
            if (isOver) {
                // 如果继续，重新初始化棋盘，继续游戏
                if (isReplay(chessman)) {
                    chessboard.initBoard();
                    chessboard.printBoard();
                    continue;
                }
                // 如果不继续
                break;
            }
            chessboard.printBoard();
            System.out.println("请输入您下棋的坐标，应以x, y格式输入：");
        }
    }

    /**
     * 是否重新开始下棋
     * @param chessman
     *          "●"为用户，"○"为计算机
     * @return 开始为true，反则返回false。
     */

    private boolean isReplay(String chessman) throws Exception{
        chessboard.printBoard();
        String message = chessman.equals(Chessman.BLACK.getChessman()) ? "恭喜您，您赢了，"
                : "分遗憾，您输了，";
        System.out.println(message + "再下一局？(y/n)");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        boolean flag = false;
        do {
            String s = br.readLine();
            if (s.equals("y")) {
                return true;
            }
            else if (s.equals("n")) {
                return false;
            } else {
                System.out.println("请重新输入：");
                flag = true;
            }
        } while (flag);
        return true;
    }

    /**
     *
     * 计算机随机下棋
     */
    private int[] computerDo() {
        int posX = (int) (Math.random() * (Chessboard.BOARD_SIZE - 1));
        int posY = (int) (Math.random() * (Chessboard.BOARD_SIZE - 1));
        String[][] board = chessboard.getBoard();
        while (board[posX][posY] != "十") {
            posX = (int) (Math.random() * (Chessboard.BOARD_SIZE - 1));
            posY = (int) (Math.random() * (Chessboard.BOARD_SIZE - 1));
        }
        int[] result = {posX, posY};
        return  result;
    }

    /**
     * 判断输赢
     * @param posX
     *          棋子的X坐标
     * @param posY
     *          棋子的Y坐标
     * @param ico
     *          棋子的类型
     * @return
     *          如果有五颗相邻棋子连成一条直线，返回真，否则返回假
     */
    private boolean isWon(int posX, int posY, String ico) {
        // 直线起点的X坐标
        int startX  = 0;
        // 直线的起点Y坐标
        int startY = 0;
        // 直线的结束点坐标
        int endX = Chessboard.BOARD_SIZE - 1;
        // 直线的结束Y坐标
        int endY = endX;
        // 同一条线上相邻棋子累积树
        int sameCount = 0;
        int temp = 0;
        // 计算起点的最小X坐标与Y坐标
        temp = posX - WIN_COUNT + 1;
        startX = temp < 0 ? 0 : temp;
        temp = posY - WIN_COUNT + 1;
        startY = temp < 0 ? 0: temp;
        // 计算终点的最大X坐标与Y坐标
        temp = posX + WIN_COUNT - 1;
        endX = temp > Chessboard.BOARD_SIZE - 1 ? Chessboard.BOARD_SIZE - 1 : temp;
        temp = posY + WIN_COUNT - 1;
        endY = temp > Chessboard.BOARD_SIZE - 1 ? Chessboard.BOARD_SIZE - 1 : temp;
        // 从左向右方向计算相邻棋子的数目
        String[][] board = chessboard.getBoard();
        for (int i = startY; i < endY; i++) {
            if (board[posX][i] == ico && board[posX][i + 1] == ico) {
                sameCount++;
            } else if (sameCount != WIN_COUNT - 1) {
                sameCount = 0;
            }
        }
        if (sameCount == 0) {
            // 从上到下方向计算相邻棋子的数目
            for (int i = startX; i < endX; i++) {
                if (board[i][posY] == ico && board[i +1][posY] == ico) {
                    sameCount++;
                } else if (sameCount != WIN_COUNT - 1) {
                    sameCount = 0;
                }
            }
        }
        if (sameCount == 0) {
            // 从左上到右下计算相同相邻棋子的数目
            int j = startY;
            for (int i = startX; i < endX; i++) {
                if (j < endY) {
                    if (board[i][j] == ico && board[i + 1][j + 1] == ico) {
                        sameCount++;
                    } else if (sameCount != WIN_COUNT - 1) {
                        sameCount = 0;
                    }
                    j++;
                }
            }
        }
        if (sameCount == 0) {
            int i = endX;
            for (int j = startY; j < endY; j++) {
                if (i > startX) {
                    if (board[i][j] == ico && board[i-1][j+1] == ico) {
                        sameCount++;
                    } else if (sameCount != WIN_COUNT - 1) {
                        sameCount = 0;
                    }
                    i--;
                }
            }
        }
        return sameCount == WIN_COUNT - 1 ? true : false;
    }

    public static void main(String[] args) throws Exception{
        GobangGame gb = new GobangGame(new Chessboard());
        gb.start();
    }
}
