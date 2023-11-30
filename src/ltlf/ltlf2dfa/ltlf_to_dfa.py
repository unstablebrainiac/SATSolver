from ltlf2dfa.parser.ltlf import LTLfParser

import signal


class timeout:
    def __init__(self, seconds=1, error_message='Timeout'):
        self.seconds = seconds
        self.error_message = error_message

    def handle_timeout(self, signum, frame):
        raise TimeoutError(self.error_message)

    def __enter__(self):
        signal.signal(signal.SIGALRM, self.handle_timeout)
        signal.alarm(self.seconds)

    def __exit__(self, type, value, traceback):
        signal.alarm(0)


def process_input(input_str):
    parser = LTLfParser()
    formula = parser(input_str)

    dfa = formula.to_dfa()
    print(dfa)  # Print the resulting DFA


if __name__ == "__main__":
    import sys

    if len(sys.argv) != 2:
        print("Usage: python script.py <ltlf_formula>")
        sys.exit(1)

    input_str = sys.argv[1]
    with timeout(seconds=60):
        process_input(input_str)
