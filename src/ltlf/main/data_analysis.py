import csv
import matplotlib.pyplot as plt


def read_data():
    with open('../../../resources/ltlf/data.csv', 'r') as file:
        reader = csv.DictReader(file)
        return list(reader)


def compute_median_time(time_data):
    time_data.sort()
    if len(time_data) % 2 == 1:
        return time_data[len(time_data) // 2]
    else:
        return (time_data[len(time_data) // 2] + time_data[len(time_data) // 2 - 1]) / 2


def plot_time_vs_final_states(data):
    filtered_data = [row for row in data if row['experimentType'] == 'RandomGraph' and row['finalStatesRandomized'] == 'false']
    for size in ['1000']:
        filtered_data_size = [row for row in filtered_data if row['size'] == size]
        unique_final_states = sorted(set([int(row['finalStates']) for row in filtered_data_size]))
        bfs_time_for_final_states = []
        random_time_for_final_states = []
        for final_state in unique_final_states:
            filtered_data_final_state = [row for row in filtered_data_size if int(row['finalStates']) == final_state]
            bfs_time_for_final_states.append(compute_median_time([int(row['bfsTime']) for row in filtered_data_final_state]))
            random_time_for_final_states.append(compute_median_time([int(row['randomTime']) for row in filtered_data_final_state]))
        plt.plot(unique_final_states, bfs_time_for_final_states, label='BFS')
        plt.plot(unique_final_states, random_time_for_final_states, label='Random')
        plt.ylim(0, 6000)
        plt.xticks(range(0, int(size), int(size) // 10))
        plt.ylabel('Median Time (ms)')
        plt.xlabel('Number of Final States')
        # plt.title('Median Time vs Number of Final States\nRandom Graphs of size ' + size + '\nRandomized Final States')
        plt.title('Median Time vs Number of Final States\nRandom Graphs of size ' + size + '\nIncremental Final States')
        plt.legend()
        # plt.show()
        # plt.savefig('../../../resources/ltlf/median_time_vs_final_states_' + size + '_random.png', dpi=1200)
        plt.savefig('../../../resources/ltlf/median_time_vs_final_states_' + size + '_incremental.png', dpi=1200)


def plot_time_vs_propositions(data):
    filtered_data = [row for row in data if row['experimentType'] == 'RandomLTLf']
    print(filtered_data)
    propositions_choices = ['10', '20', '30']
    bfs_time_for_propositions = []
    random_time_for_propositions = []
    for propositions in propositions_choices:
        filtered_data_propositions = [row for row in filtered_data if row['size'] == str(propositions)]
        print(filtered_data_propositions)
        bfs_time_for_propositions.append(compute_median_time([int(row['bfsTime']) for row in filtered_data_propositions]))
        random_time_for_propositions.append(compute_median_time([int(row['randomTime']) for row in filtered_data_propositions]))
    plt.plot(propositions_choices, bfs_time_for_propositions, label='BFS')
    plt.plot(propositions_choices, random_time_for_propositions, label='Random')
    plt.ylim(0, 10000)
    plt.ylabel('Median Time (ms)')
    plt.xlabel('Number of Propositions')
    plt.title('Median Time vs Number of Propositions\nRandom LTLf Formulas of size 30')
    plt.legend()
    # plt.show()
    plt.savefig('../../../resources/ltlf/median_time_vs_propositions.png', dpi=1200)


def plot_datasets(data):
    filtered_data = [row for row in data if row['experimentType'].startswith('LTLfDataset')]
    bfs_time = [int(row['bfsTime']) for row in filtered_data]
    random_time = [int(row['randomTime']) for row in filtered_data]
    plt.scatter(bfs_time, random_time)
    upper_bound = 2500
    plt.xlim(0, upper_bound)
    plt.ylim(0, upper_bound)
    plt.xlabel('BFS Time (ms)')
    plt.ylabel('Random Time (ms)')
    plt.title('BFS Time vs Random Time\nLTLf Dataset')
    # plt.show()
    plt.savefig('../../../resources/ltlf/bfs_vs_random_' + str(upper_bound) + '.png', dpi=1200)

def main():
    data = read_data()
    # plot_time_vs_final_states(data)
    # plot_time_vs_propositions(data)
    plot_datasets(data)


if __name__ == "__main__":
    main()