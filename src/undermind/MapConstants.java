package undermind;

/**
 * Created By: Itay Sabato<br/>
 * Date: 17/07/11 <br/>
 * Time: 13:15 <br/>
 */
public enum MapConstants {
    Benzene {
        @Override
        public String getHash() {
            return "af618ea3ed8a8926ca7b17619eebcb9126f0d8b1";
        }
    },
    Destination {
        @Override
        public String getHash() {
            return "4e24f217d2fe4dbfa6799bc57f74d8dc939d425b";
        }
    },
    Heartbreak_Ridge {
        @Override
        public String getHash() {
            return "6f8da3c3cc8d08d9cf882700efa049280aedca8c";
        }
    },
    Aztec {
        @Override
        public String getHash() {
            return "ba2fc0ed637e4ec91cc70424335b3c13e131b75a";
        }
    },
    Tau_Cross {
        @Override
        public String getHash() {
            return "9bfc271360fa5bab3707a29e1326b84d0ff58911";
        }
    },
    Andromeda {
        @Override
        public String getHash() {
            return "1e983eb6bcfa02ef7d75bd572cb59ad3aab49285";
        }
    },
    Circuit_Breaker {
        @Override
        public String getHash() {
            return "450a792de0e544b51af5de578061cb8a2f020f32";
        }
    },
    Empire_of_The_Sun {
        @Override
        public String getHash() {
            return "'a220d93efdf05a439b83546a579953c63c863ca7";
        }
    },
    Fortress {
        @Override
        public String getHash() {
            return "83320e505f35c65324e93510ce2eafbaa71c9aa1";
        }
    },
    Python {
        @Override
        public String getHash() {
            return "de2ada75fbc741cfa261ee467bf6416b10f9e301";
        }
    };

    public abstract String getHash();
}
