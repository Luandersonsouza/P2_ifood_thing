package MyFood;

import easyaccept.EasyAccept;

public class Main {
    public Main() {
    }

    public static void main(String[] args) {
        EasyAccept.main(new String[]{"MyFood.Facade", "tests/us1_1.txt"});
        EasyAccept.main(new String[]{"MyFood.Facade", "tests/us1_2.txt"});
        EasyAccept.main(new String[]{"MyFood.Facade", "tests/us2_1.txt"});
        EasyAccept.main(new String[]{"MyFood.Facade", "tests/us2_2.txt"});
        EasyAccept.main(new String[]{"MyFood.Facade", "tests/us3_1.txt"});
        EasyAccept.main(new String[]{"MyFood.Facade", "tests/us3_2.txt"});
        EasyAccept.main(new String[]{"MyFood.Facade", "tests/us4_1.txt"});
        EasyAccept.main(new String[]{"MyFood.Facade", "tests/us4_2.txt"});
    }
}
