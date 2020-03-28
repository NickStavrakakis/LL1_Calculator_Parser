import java.io.InputStream;
import java.io.IOException;

class CalculatorEval {

	private int lookaheadToken;

	private InputStream in;

	public CalculatorEval(InputStream in) throws IOException {
		this.in = in;
		lookaheadToken = in.read();
	}

	private void consume(int symbol) throws IOException, ParseError {
		if (lookaheadToken != symbol)
			throw new ParseError();
		lookaheadToken = in.read();
	}

	private int evalDigit(int digit) {
		return digit - '0';
	}

	private int Exp() throws IOException, ParseError {
		if ( (lookaheadToken < '0' || lookaheadToken > '9') && lookaheadToken != '(')
			throw new ParseError();
		int term = Term();
		int exp1 = Exp1(term);
		return exp1;
	}

	private int Exp1(int left_term) throws IOException, ParseError {
		if (lookaheadToken != '+' && lookaheadToken != '-') // || lookaheadToken == '\n' || lookaheadToken == -1
			return left_term;

		if (lookaheadToken == '+') {
			consume('+');
			int current_result = left_term + Term();
			int exp1 = Exp1(current_result);
			return exp1;
		}
		else {
			consume('-');
			int current_result = left_term - Term();
			int exp1 = Exp1(current_result);
			return exp1;
		}
	}

	private int Term() throws IOException, ParseError {
		if ( (lookaheadToken < '0' || lookaheadToken > '9') && lookaheadToken != '(')
			throw new ParseError();
		int factor = Factor();
		int term = Term1(factor);
		if (term == -1)
			return factor;
		return term;
	}

	private int Term1(int left_term) throws IOException, ParseError {
		if (lookaheadToken != '*' && lookaheadToken != '/')
			return left_term;

		if (lookaheadToken == '*') {
			consume('*');
			int current_result = left_term*Factor();
			int exp1 = Term1(current_result);
			return exp1;
		}
		else {
			consume('/');
			int current_result = left_term/Factor();
			int exp1 = Term1(current_result);
			return exp1;
		}
 	}

	private int Factor() throws IOException, ParseError {
		if (lookaheadToken < '0' || lookaheadToken > '9') {
			if (lookaheadToken != '(')
				throw new ParseError();
			consume('(');
			int inside_par = Exp();
			consume(')');
			return inside_par;
		}
		return Digits();
	}

	private int Digits() throws IOException, ParseError {
		if (lookaheadToken < '0' || lookaheadToken > '9')
			throw new ParseError();

		int number = Digit();
		int new_digit = More();
		int n = 1;

		while (new_digit != -1){
			n = n*10;
			number = number*n + new_digit;
			new_digit = More();
		}
		return number;
	}

	private int More() throws IOException, ParseError {
		if (lookaheadToken < '0' || lookaheadToken > '9')
			return -1;
		return Digit();
	}

	private int Digit() throws IOException, ParseError {
		if(lookaheadToken < '0' || lookaheadToken > '9')
			throw new ParseError();
		int digit = evalDigit(lookaheadToken);
		consume(lookaheadToken);
		return digit;
	}

	public int eval() throws IOException, ParseError {
		int rv = Exp();
		if (lookaheadToken != '\n' && lookaheadToken != -1)
			throw new ParseError();
		return rv;
	}

	public static void main(String[] args) {
		try {
			CalculatorEval evaluate = new CalculatorEval(System.in);
			System.out.println(evaluate.eval());
		}
		catch (IOException e) {
			System.err.println(e.getMessage());
		}
		catch(ParseError err){
			System.err.println(err.getMessage());
		}
	}
}
