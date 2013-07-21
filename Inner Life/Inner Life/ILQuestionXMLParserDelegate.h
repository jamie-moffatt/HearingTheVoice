//
//  ILQuestionXMLParserDelegate.h
//  Inner Life
//
//  Created by Matthew Bates on 13/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ILSection.h"
#import "ILQuestion.h"
#import "ILChoice.h"

@interface ILQuestionXMLParserDelegate : NSObject <NSXMLParserDelegate>
{
    @private
    ILSection  *_currentSection;
    ILQuestion *_currentQuestion;
    NSMutableArray *_currentQuestionList;
    NSMutableArray *_currentChoiceList;
}

// This is the array that should be populated with ILSection instances by the parser
@property NSMutableArray *sectionList;

@end