import csv
import os

import matplotlib.pyplot as plt


def read_data():
    with open('../../resources/sat/data.csv', 'r') as file:
        reader = csv.DictReader(file)
        return list(reader)


def compute_median_time(data, solver, N, LbyN):
    filtered_data = [row for row in data if row['Solver'] == solver and row['N'] == N and row['L/N'] == LbyN]

    if len(filtered_data) != 100:
        raise ValueError('Data size not 100', len(filtered_data), filtered_data, solver, N, LbyN)

    filtered_data.sort(key=lambda row: int(row['Time(ms)']))

    return (int(filtered_data[49]['Time(ms)']) + int(filtered_data[50]['Time(ms)'])) / 2


def compute_median_dpll_calls(data, solver, N, LbyN):
    filtered_data = [row for row in data if row['Solver'] == solver and row['N'] == N and row['L/N'] == LbyN]

    if len(filtered_data) != 100:
        raise ValueError('Data size not 100', len(filtered_data), filtered_data, solver, N, LbyN)

    for row in filtered_data:
        if int(row['DPLL Calls']) == 0:
            row['DPLL Calls'] = 100
    filtered_data.sort(key=lambda row: int(row['DPLL Calls']))
    print(filtered_data[49]['DPLL Calls'], filtered_data[50]['DPLL Calls'])

    return (int(filtered_data[49]['DPLL Calls']) + int(filtered_data[50]['DPLL Calls'])) / 2


def plot_time_and_dpll_calls(data):
    solvers = ['RandomDPLLSolver', 'TwoClauseDPLLSolver', 'TwoClauseMajoritySelectionDPLLSolver']
    N = '150'
    LbyNs = ['3.0', '3.2', '3.4', '3.6', '3.8', '4.0', '4.2', '4.4', '4.6', '4.8', '5.0', '5.2', '5.4', '5.6', '5.8', '6.0']

    for solver in solvers:
        median_times = [compute_median_time(data, solver, N, LbyN) for LbyN in LbyNs]
        plt.plot(LbyNs, [median_time / 1000 for median_time in median_times], label=solver)
        plt.ylabel('Median Time (s)')
        plt.xlabel('L/N')
        plt.title('Median Time vs L/N\n' + solver)
        plt.savefig('../../resources/' + solver + '_median_time.png')
        plt.show()

        median_dpll_calls = [compute_median_dpll_calls(data, solver, N, LbyN) for LbyN in LbyNs]
        plt.plot(LbyNs, median_dpll_calls, label=solver)
        plt.ylabel('Median Number of DPLL Calls')
        plt.xlabel('L/N')
        plt.title('Median Number of DPLL Calls vs L/N\n' + solver)
        plt.savefig('../../resources/' + solver + '_median_dpll_calls.png')
        plt.show()


def compute_probability_of_satisfiability(data):
    Ns = ['100', '125', '150']
    LbyNs = ['3.0', '3.2', '3.4', '3.6', '3.8', '4.0', '4.2', '4.4', '4.6', '4.8', '5.0', '5.2', '5.4', '5.6', '5.8', '6.0']

    probabilities_for_N = {N: [] for N in Ns}

    for N in Ns:
        for LbyN in LbyNs:
            filtered_data = [row for row in data if row['N'] == N and row['L/N'] == LbyN]
            satisfiable_count = 0
            seeds = set([row['Seed'] for row in filtered_data])
            if len(seeds) != 100:
                raise ValueError('Data size not 100', len(seeds), filtered_data, N, LbyN)
            for seed in seeds:
                data_for_seed = [row for row in filtered_data if row['Seed'] == seed]
                # if len(data_for_seed) != 3:
                #     raise ValueError('Data size not 3', len(data_for_seed), data_for_seed, N, LbyN, seed)
                data_without_timeout = [row for row in data_for_seed if int(row['DPLL Calls']) != 100]
                if len(data_without_timeout) == 0:
                    raise ValueError('All solvers timed out', data_for_seed, N, LbyN, seed)
                # if not all values of satisfiable are the same, then raise an error
                if len(set([row['Satisfiable'] for row in data_without_timeout])) != 1:
                    raise ValueError('Not all solvers agree on satisfiability', data_without_timeout, N, LbyN, seed)
                if data_without_timeout[0]['Satisfiable'] == 'true':
                    satisfiable_count += 1

            probabilities_for_N[N].append(satisfiable_count / 100)

    plt.plot(LbyNs, probabilities_for_N['100'], label='N = 100')
    plt.plot(LbyNs, probabilities_for_N['125'], label='N = 125')
    plt.plot(LbyNs, probabilities_for_N['150'], label='N = 150')
    plt.ylabel('Probability of Satisfiability')
    plt.xlabel('L/N')
    plt.title('Probability of Satisfiability vs L/N')
    plt.legend()
    plt.savefig('../../resources/probability_of_satisfiability.png')
    plt.show()


def compare_solver_to_random(data):
    N = '150'
    LbyNs = ['3.0', '3.2', '3.4', '3.6', '3.8', '4.0', '4.2', '4.4', '4.6', '4.8', '5.0', '5.2', '5.4', '5.6', '5.8', '6.0']

    median_times = [compute_median_time(data, 'TwoClauseMajoritySelectionDPLLSolver', N, LbyN) for LbyN in LbyNs]
    random_median_times = [compute_median_time(data, 'RandomDPLLSolver', N, LbyN) for LbyN in LbyNs]

    median_dpll_calls = [compute_median_dpll_calls(data, 'TwoClauseMajoritySelectionDPLLSolver', N, LbyN) for LbyN in LbyNs]
    random_median_dpll_calls = [compute_median_dpll_calls(data, 'RandomDPLLSolver', N, LbyN) for LbyN in LbyNs]

    plt.plot(LbyNs, [median_time / 1000 for median_time in median_times], label='Two Clause Majority Selection DPLL Solver')
    plt.plot(LbyNs, [median_time / 1000 for median_time in random_median_times], label='Random DPLL Solver')
    plt.ylabel('Median Time (s)')
    plt.xlabel('L/N')
    plt.title('Median Time vs L/N\nTwo Clause Majority Selection DPLL Solver vs Random DPLL Solver')
    plt.legend()
    plt.savefig('../../resources/median_time_vs_LbyN.png')
    plt.show()

    plt.plot(LbyNs, median_dpll_calls, label='Two Clause Majority Selection DPLL Solver')
    plt.plot(LbyNs, random_median_dpll_calls, label='Random DPLL Solver')
    plt.ylabel('Median Number of DPLL Calls')
    plt.xlabel('L/N')
    plt.title('Median Number of DPLL Calls vs L/N\nTwo Clause Majority Selection DPLL Solver vs Random DPLL Solver')
    plt.legend()
    plt.savefig('../../resources/median_dpll_calls_vs_LbyN.png')
    plt.show()

    ratio = [median_times[i] / random_median_times[i] for i in range(len(median_times))]

    plt.plot(LbyNs, ratio, label='Ratio')
    plt.ylabel('Ratio of Median Time')
    plt.xlabel('L/N')
    plt.title('Ratio of Median Time vs L/N\nTwo Clause Majority Selection DPLL Solver vs Random DPLL Solver')
    plt.savefig('../../resources/ratio_of_median_time_vs_LbyN.png')
    plt.show()


def compare_solver_to_two_clause(data):
    N = '150'
    LbyNs = ['3.0', '3.2', '3.4', '3.6', '3.8', '4.0', '4.2', '4.4', '4.6', '4.8', '5.0', '5.2', '5.4', '5.6', '5.8', '6.0']

    median_times = [compute_median_time(data, 'TwoClauseMajoritySelectionDPLLSolver', N, LbyN) for LbyN in LbyNs]
    two_clause_median_times = [compute_median_time(data, 'TwoClauseDPLLSolver', N, LbyN) for LbyN in LbyNs]

    median_dpll_calls = [compute_median_dpll_calls(data, 'TwoClauseMajoritySelectionDPLLSolver', N, LbyN) for LbyN in LbyNs]
    two_clause_median_dpll_calls = [compute_median_dpll_calls(data, 'TwoClauseDPLLSolver', N, LbyN) for LbyN in LbyNs]

    plt.plot(LbyNs, [median_time / 1000 for median_time in median_times], label='Two Clause Majority Selection DPLL Solver')
    plt.plot(LbyNs, [median_time / 1000 for median_time in two_clause_median_times], label='Two Clause DPLL Solver')
    plt.ylabel('Median Time (s)')
    plt.xlabel('L/N')
    plt.title('Median Time vs L/N\nTwo Clause Majority Selection DPLL Solver vs Two Clause DPLL Solver')
    plt.legend()
    plt.savefig('../../resources/median_time_vs_LbyN_two_clause.png')
    plt.show()

    plt.plot(LbyNs, median_dpll_calls, label='Two Clause Majority Selection DPLL Solver')
    plt.plot(LbyNs, two_clause_median_dpll_calls, label='Two Clause DPLL Solver')
    plt.ylabel('Median Number of DPLL Calls')
    plt.xlabel('L/N')
    plt.title('Median Number of DPLL Calls vs L/N\nTwo Clause Majority Selection DPLL Solver vs Two Clause DPLL Solver')
    plt.legend()
    plt.savefig('../../resources/median_dpll_calls_vs_LbyN_two_clause.png')
    plt.show()

    ratio = [median_times[i] / two_clause_median_times[i] for i in range(len(median_times))]

    plt.plot(LbyNs, ratio, label='Ratio')
    plt.ylabel('Ratio of Median Time')
    plt.xlabel('L/N')
    plt.title('Ratio of Median Time vs L/N\nTwo Clause Majority Selection DPLL Solver vs Two Clause DPLL Solver')
    plt.savefig('../../resources/ratio_of_median_time_vs_LbyN_two_clause.png')
    plt.show()


def main():
    data = read_data()
    plot_time_and_dpll_calls(data)
    compute_probability_of_satisfiability(data)
    compare_solver_to_random(data)
    compare_solver_to_two_clause(data)


if __name__ == "__main__":
    main()
