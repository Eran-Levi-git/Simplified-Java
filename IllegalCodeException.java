package oop.ex6.main;

class IllegalCodeException extends Exception {
    // Data members
    private String message;

    // Constructor
    IllegalCodeException(String message){
        super();
        this.message = message;
    }

    /**
     * print an informative message concerning the s-java rule violation.
     */
    public void printMessage(){
        System.err.println(message);
    }
}
