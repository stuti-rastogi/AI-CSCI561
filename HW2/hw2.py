import copy
import sys
import time
import operator
import math

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
		new_state=State(self.n, self.p, self.t, [], startTime)
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
		for i in range(p):
			positions = []
			for j in range(n):
				for k in range(n):
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

		for i in range(n):
			for j in range(n):
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

		if ((time.clock() - state.startTime) > (state.t - 0.5)):
			return None

		maxMove, value = self.maxValue(state, self.minInf, self.maxInf, 0)
		return maxMove
		
	def maxValue(self, state, alpha, beta, d):
		self.calls = self.calls + 1
		# print "In Max: calls = " + str(self.calls)
		
		#state.printBoard()
		bestMove = None
		score = state.score
		# print "Score: " + str(score)
		#print state.t
		if ((time.clock() - state.startTime) > (state.t - 0.5)):
			return bestMove, score

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
				if ((time.clock() - state.startTime) > (state.t - 0.5)):
					return bestMove, score
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
		if ((time.clock() - state.startTime) > (state.t - 0.5)):
			return bestMove, score

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
				if ((time.clock() - state.startTime) > (state.t - 0.5)):
					return bestMove, score
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
	startTime = time.clock()
	with open ("input.txt", "r") as inputfile:
		with open ("calibrate.txt", "r") as calibratefile:
			nodesPerSecond = float(calibratefile.readline().strip())
			inputdata = inputfile.readlines()

			n = int(inputdata[0].strip())
			p = int(inputdata[1].strip())
			t = float(inputdata[2].strip())
			starsCount = 0
			
			board = []
			for i in range(n):
				line = inputdata[3+i].strip()
				row = []
				for x in line:
					if (x == "*"):
						row.append(-1)
						starsCount = starsCount + 1
					else:
						row.append(int(x))
				board.append(row)
			
			s = State(n, p, t, board, startTime)
			allInitialActions = s.getLegalActions()
			allInitialActions.sort(key=len, reverse=True)

			maxLength = float(len(allInitialActions[0]))
			movesLength = float(len(allInitialActions))
			remainingFruits = pow(n,2) - starsCount

			timeToUse = t * movesLength/remainingFruits

			s1 = State(n, p, timeToUse, board, startTime)
			#print "t: " + str(t)
			#print "calculated t: " + str(timeToUse)

			# agent = Agent(3)

			b = float(remainingFruits)/p
			# print b
			maxCalls = nodesPerSecond * t
			if (b <= 1):
				d = 2

			else:	
				d = math.log(((b-1) * maxCalls) + 1, b) - 1
			#print "d: " + str(d)
			#print allInitialActions[0]

			agent = Agent(int(round(d)))
			bestAction = agent.getAction(s1)
			
			if (bestAction == None):
				bestAction = allInitialActions[0]
			#print bestAction, len(bestAction)

			s.generateSuccessor(bestAction).writeOutput(bestAction)
			print str(time.clock()-startTime) + " seconds."

