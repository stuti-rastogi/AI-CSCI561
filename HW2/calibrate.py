import copy
import sys
import time
import operator

class State:
	def __init__(self, n, p, t, board, startTime):
		self.n = n
		self.p = p
		self.t = t
		self.board=board
		self.score = 0
		self.startTime = startTime
		
	def __eq__(self, other):
		return tuple(self.board)==tuple(other.board)
		
	def __hash__(self):
		return hash(tuple(map(tuple,self.board)))
	
	def copy(self):
		new_state=State(self.n,self.p,self.t,[], self.startTime)
		new_state.board=copy.deepcopy(self.board)
		return new_state

	def printBoard(self):
		print '\n'.join(''.join(str(i) + "\t" for i in row) for row in self.board)

	def writeOutput(self, action):
		with open ("output.txt", "w") as outputfile:
			pos = action[0]
			alphabet = ['A', 'B', 'C', 'D', 'E', 'F',
						'G', 'H', 'I', 'J', 'K', 'L',
						'M', 'N', 'O', 'P', 'Q', 'R',
						'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z']
			outputfile.write(alphabet[pos[1]] + str(pos[0]+1) + "\n")
			for row in self.board:
				for x in row:
					if (x == -1):
						outputfile.write("*")
					else:
						outputfile.write(str(x))
				outputfile.write("\n")

	def isTerminalState(self):
		starsCount = 0
		for row in self.board:
			starsCount = starsCount + row.count(-1)
		if (starsCount == pow(self.n, 2)):
			return True
		return False

	def eval(self):
		starsCount = 0
		for row in self.board:
			starsCount = starsCount + row.count(-1)
		remainingFruits = pow(self.n,2) - starsCount
		if (remainingFruits == 0):
			return self.score
		return self.score/pow(remainingFruits,2)

	# get all valid moves as list of [(co-ordinates), score]
	def getLegalActions(self):
		moves = []
		fruitPositions = {}
		for i in range(self.p):
			positions = []
			for j in range(self.n):
				for k in range(self.n):
					if (self.board[j][k] == i):
						positions.append((j, k))
			fruitPositions[i] = positions

		for fruit in fruitPositions.keys():
			currentList = fruitPositions[fruit]
			for i in range(len(currentList)):
				pos = currentList[i]
				currentMove = []
				found = False
				possibleNeighbors = [(pos[0], pos[1]+1), (pos[0], pos[1]-1), (pos[0]+1, pos[1]), (pos[0]-1, pos[1])]
				connectedNeighbors = []
				for j in range(i):
					if (currentList[j] in possibleNeighbors):
						connectedNeighbors.append(currentList[j])

				if (len(connectedNeighbors) == 0):
					currentMove.append(pos)
					moves.append(currentMove)
				elif (len(connectedNeighbors) == 1):
					for eachMove in moves:
						if (connectedNeighbors[0] in eachMove):
							eachMove.append(pos)
							break
				else:
					for eachMove in moves:
						if (connectedNeighbors[0] in eachMove):
							currentMove.extend(eachMove)
							currentMove.append(pos)
							moves.remove(eachMove)
							break
					for j in range(1, len(connectedNeighbors)):
						for eachMove in moves:
							if (connectedNeighbors[j] in eachMove):
								currentMove.extend(eachMove)
								currentMove = list(set(currentMove))
								moves.remove(eachMove)
					moves.append(currentMove)

		return moves

	#applying an action, get the new board with gravity applied
	def generateSuccessor(self, action):
		newState = self.copy()
		for pos in action:
			newState.board[pos[0]][pos[1]] = -1

		for i in range(self.n):
			for j in range(self.n):
				if (newState.board[i][j] == -1):
					k = 0
					for k in range(i-1, -1, -1):
						newState.board[k+1][j] = newState.board[k][j]
					newState.board[k][j] = -1
		newState.score = pow(len(action),2)
		return newState

class Agent:
	def __init__(self, args):
		self.depth = args
		self.minInf = -sys.maxint - 1
		self.maxInf = sys.maxint
		self.calls = 0

	def getEval(self, state, action, newScore):
		starsCount = 0
		for row in state.board:
			starsCount = starsCount + row.count(-1)
		remainingFruits = pow(state.n,2) - starsCount - len(action)
		if (remainingFruits == 0):
			return newScore
		return float(newScore/pow(remainingFruits,2))
		
	def getAction(self, state):
		#print time.clock() - state.startTime
		maxMove, value = self.maxValue(state, self.minInf, self.maxInf, 0)

		# if ((time.clock() - state.startTime) > (state.t - 0.5)):
		# 	for i in range(n):
		# 		for j in range(n):
		# 			if (x != -1):
		# 				return [(i, j)]
		return maxMove
		
	def maxValue(self, state, alpha, beta, d):
		self.calls = self.calls + 1
		# print "In Max: calls = " + str(self.calls)
		
		#state.printBoard()
		bestMove = None
		score = state.score
		# print "Score: " + str(score)

		v = self.minInf						# -infinity
	
		if (state.isTerminalState()):
			return bestMove, state.eval()
		if (d == self.depth):
			return bestMove, score
		else:
			allActions = state.getLegalActions()
			# print allActions
			#print allActions
			actions = []
			for action in allActions:
				actions.append((action, self.getEval(state, action, state.score + pow(len(action), 2))))

			actions.sort(key=operator.itemgetter(1), reverse=True)
			# print actions
			for action in actions:
				nextState = state.generateSuccessor(action[0])
				score = state.score + nextState.score
				nextState.score = score
				x, compare = self.minValue(nextState, alpha, beta, d+1)
				#print "Min retuned move: " + str(x) + " and value: " + str(compare)
				if v < compare:
					bestMove = action[0]			#new Max
					v = compare
				if v >= beta:
					return bestMove, v 			#pruning
				alpha = max(alpha, v)
				#print "Action: " + str(action) + "; Alpha: " + str(alpha) + "; Beta: " + str(beta) + "; v: " + str(v)

			return bestMove, v
		
	def minValue(self, state, alpha, beta, d):
		self.calls = self.calls + 1
		# print "In Min: calls = " + str(self.calls)
		
		#state.printBoard()
		bestMove = None
		score = state.score
		# print "Score: " + str(score)
		v = self.maxInf
		
		if (state.isTerminalState()):
			return bestMove, score
		if (d == self.depth):
			#cutoff here
			return bestMove, state.eval()			
		else:
			allActions = state.getLegalActions()
			actions = []
			for action in allActions:
				actions.append((action, self.getEval(state, action, state.score - pow(len(action), 2))))

			actions.sort(key=operator.itemgetter(1))
			# print actions
			for action in actions:
				nextState = state.generateSuccessor(action[0])
				score = state.score - nextState.score
				nextState.score = score
				x, compare = self.maxValue(nextState, alpha, beta, d+1)
				#print "Max retuned move: " + str(x) + " and value: " + str(compare)
				if v > compare:
					bestMove = action[0]			#new Min
					v = compare
				if v <= alpha:
					return bestMove, v
				beta = min(beta, v)
				#print "Action: " + str(action) + "; Alpha: " + str(alpha) + "; Beta: " + str(beta) + "; v: " + str(v)
			return bestMove, v


if __name__ == '__main__':
	veryStart = time.clock()
	n = [10, 26, 10, 15]
	p = [7, 9, 4, 7]
	depth = [4, 2, 5, 3]
	remainingFruits = [0,0,0,0]
	weightedSum = 0

	board = [
				[
					[3,1,-1,-1,-1,2,2,3,6,0],
					[3,2,-1,-1,-1,3,2,0,6,6],
					[3,4,2,-1,-1,1,4,2,1,3],
					[3,5,4,-1,0,3,1,1,4,4],
					[3,6,6,-1,0,1,1,0,1,4],
					[3,3,2,-1,3,6,6,6,4,4],
					[3,3,3,-1,0,2,2,0,1,2],
					[3,2,4,-1,2,0,2,3,2,1],
					[3,1,3,-1,0,5,4,0,0,5],
					[3,3,5,-1,0,5,5,2,4,5]
				],
				[
					[1,0,0,2,7,6,4,9,9,9,3,3,8,8,2,2,8,0,0,0,2,2,5,7,6,3],
					[1,1,0,3,4,6,6,6,6,4,4,4,9,8,8,8,8,8,8,3,0,0,0,2,8,5],
					[0,0,0,2,7,6,4,9,7,6,8,3,7,4,9,3,3,8,8,8,0,0,2,7,6,3],
					[3,3,3,4,2,7,6,4,9,6,9,3,3,8,8,8,8,8,0,0,0,2,2,4,3,6],
					[7,5,4,4,3,2,6,6,6,7,9,3,3,8,8,2,2,8,0,0,2,2,5,7,6,3],
					[4,3,4,5,2,7,6,4,9,9,9,3,3,8,8,4,2,8,0,0,0,2,2,5,9,3],
					[5,5,5,2,7,6,4,9,9,9,3,3,8,8,2,2,8,0,0,0,2,1,0,0,3,0],
					[7,8,8,8,3,7,6,6,2,9,3,3,8,8,8,8,8,0,0,0,2,2,5,7,6,3],
					[7,9,0,2,3,7,7,1,2,5,5,5,5,5,8,8,8,8,8,8,8,8,8,8,8,8],
					[6,5,9,2,0,8,9,1,1,1,3,2,4,3,2,3,3,5,7,8,8,0,8,1,1,2],
					[3,3,3,4,2,7,6,4,9,9,9,3,3,8,8,8,8,8,0,0,0,2,2,4,3,6],
					[7,5,4,4,3,2,6,6,6,7,9,3,3,8,8,2,2,8,0,0,2,2,5,7,6,3],
					[1,0,0,2,7,6,4,9,9,6,3,3,8,8,2,2,8,0,3,9,2,2,5,9,2,0],
					[1,0,0,6,4,9,9,3,3,8,8,2,2,8,2,2,8,0,0,1,1,0,3,4,6,6],
					[5,2,7,3,9,9,3,9,9,9,3,3,8,8,2,2,8,0,0,0,2,2,5,7,6,3],
					[1,0,0,2,7,6,5,5,7,9,3,3,8,8,2,2,8,0,7,1,1,0,3,4,6,6],
					[3,3,3,4,2,7,6,4,9,8,9,3,3,6,6,8,8,8,0,0,0,2,2,4,3,6],
					[1,0,0,2,7,6,4,9,9,6,4,9,9,9,3,3,8,8,2,2,8,2,5,7,6,3],
					[2,4,1,2,7,6,3,2,2,9,3,3,8,5,5,8,8,0,0,0,2,1,0,0,3,0],
					[1,0,6,4,9,9,9,3,3,8,8,2,2,8,2,2,8,0,0,0,2,2,5,7,6,3],
					[1,0,0,2,7,8,4,4,4,9,3,3,8,8,2,2,8,0,0,0,2,2,5,7,6,3],
					[7,5,4,4,3,2,6,6,6,7,9,3,3,8,8,2,2,8,0,0,2,2,5,7,6,3],
					[1,0,0,2,7,6,4,4,4,4,3,3,8,8,8,5,2,8,3,0,5,5,2,1,0,0],
					[1,0,1,1,0,2,3,4,6,6,3,3,8,8,2,2,8,0,0,0,2,2,5,7,6,3],
					[1,6,0,2,7,6,4,9,9,9,3,3,7,5,4,4,3,2,6,6,6,7,9,3,3,8],
					[9,0,0,2,7,6,4,4,3,9,3,3,8,8,2,2,8,0,0,0,2,2,5,7,6,3]
				],
				[
					[3,1,0,2,3,2,2,3,1,0],
					[0,1,2,1,2,3,2,0,1,3],
					[3,0,2,1,1,1,1,1,1,3],
					[0,2,2,1,0,3,1,1,3,2],
					[0,2,3,0,0,1,1,0,1,2],
					[0,3,2,3,3,2,1,0,1,0],
					[2,0,0,3,0,2,2,0,1,2],
					[2,2,0,2,2,0,0,0,2,1],
					[0,1,3,0,0,0,0,0,2,0],
					[2,2,0,0,0,2,2,2,3,1]
				],
				[
					[1,5,0,3,3,3,3,3,3,1,1,0,4,6,2],
					[5,5,4,2,3,3,5,4,6,3,3,2,1,4,2],
					[0,0,2,4,5,3,3,3,3,3,5,4,4,6,0],
					[1,5,2,4,6,6,3,1,1,0,0,4,4,4,4],
					[4,6,2,5,2,2,3,3,1,4,0,6,6,6,4],
					[0,0,2,5,4,0,3,3,3,1,1,5,1,4,2],
					[0,2,4,5,2,4,4,4,3,3,3,3,3,3,3],
					[1,1,1,5,5,2,4,0,0,5,6,6,6,6,3],
					[2,4,6,6,5,2,1,0,0,0,3,4,2,2,6],
					[0,1,2,2,2,2,0,1,4,6,6,2,0,0,4],
					[0,0,0,0,1,4,4,2,5,6,3,3,2,0,1],
					[5,4,3,2,1,0,5,4,3,2,1,0,2,2,0],
					[6,5,3,4,2,1,4,6,3,6,6,6,2,0,0],
					[4,2,3,4,4,1,1,6,6,6,6,1,1,5,1],
					[5,5,5,5,4,4,1,6,3,2,2,2,1,1,1]
				]
			]

	for i in range(4):
		startTime = time.clock()
		s = State(n[i], p[i], 100, board[i], startTime)
		agent = Agent(depth[i])
		bestAction = agent.getAction(s)
		
		# print "# of calls: " + str(agent.calls)
		nodesPerSec = agent.calls/(time.clock()-startTime)
		#print nodesPerSec

		starsCount = 0
		for row in board[i]:
			starsCount = starsCount + row.count(-1)
		remainingFruits[i] = pow(n[i],2) - starsCount

		#print remainingFruits[i]
		
		weightedSum = weightedSum + (remainingFruits[i] * nodesPerSec)

	finalCalibration = weightedSum/sum(remainingFruits)


	with open ("calibrate.txt", "w") as outputfile:
		outputfile.write(str(finalCalibration))

	print str(time.clock()-veryStart) + " seconds."

