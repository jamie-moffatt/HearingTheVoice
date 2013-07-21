//
//  ILQuestionXMLParserDelegate.m
//  Inner Life
//
//  Created by Matthew Bates on 13/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILQuestionXMLParserDelegate.h"

@implementation ILQuestionXMLParserDelegate

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName attributes:(NSDictionary *)attributeDict
{
    if ([elementName isEqualToString:@"section"])
    {
        _currentSection = [[ILSection alloc] init];
        
        _currentSection.sectionID = [[attributeDict objectForKey:@"id"] integerValue];
        _currentSection.name = [attributeDict objectForKey:@"name"];
        _currentSection.sectionDescription = [attributeDict objectForKey:@"description"];
        
    }
    else if ([elementName isEqualToString:@"questions"])
    {
        _currentQuestionList = [[NSMutableArray alloc] init];
    }
    else if ([elementName isEqualToString:@"question"])
    {
        _currentQuestion = [[ILQuestion alloc] init];
        
        _currentQuestion.questionID = [[attributeDict objectForKey:@"id"] integerValue];
        _currentQuestion.sectionID = _currentSection.sectionID;
        _currentQuestion.number = [[attributeDict objectForKey:@"number"] integerValue];
        _currentQuestion.type = [ILQuestion typeFromString:[attributeDict objectForKey:@"type"]];
        _currentQuestion.questionDescription = [attributeDict objectForKey:@"description"];
        
        [_currentQuestionList addObject:_currentQuestion];
    }
    else if ([elementName isEqualToString:@"choices"])
    {
        _currentChoiceList = [[NSMutableArray alloc] init];
    }
    else if ([elementName isEqualToString:@"choice"])
    {
        ILChoice *choice =[[ILChoice alloc] initWithText:[attributeDict objectForKey:@"text"] andValue:[attributeDict objectForKey:@"value"]];
        [_currentChoiceList addObject:choice];
    }
}

- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName
{
    if ([elementName isEqualToString:@"section"])
    {
        [_sectionList addObject:_currentSection];
    }
    else if ([elementName isEqualToString:@"questions"])
    {
        _currentSection.questions = _currentQuestionList;
    }
    else if ([elementName isEqualToString:@"choices"])
    {
        _currentSection.choices = _currentChoiceList;
    }
}

@end
